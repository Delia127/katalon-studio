package com.kms.katalon.composer.components.impl.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class EntityIndexingUtil {

    public static final String LUCENE_DIR_LOCATION = ProjectController.getInstance().getTempDir() + File.separator
            + "plugins" + File.separator + "lucene" + File.separator;

    public static final String FIELD_ID = "id";

    public static final String FIELD_TYPE = "type";

    private IndexWriterConfig indexWriterConfig;

    private IndexWriter writer;

    private DirectoryReader reader;

    private IndexSearcher searcher;

    private QueryBuilder queryParser;

    private String indexDirLocation;

    private String projectDirLocation;

    private Directory indexDir;

    private Sort sort;

    private static EntityIndexingUtil instance;

    private EntityIndexingUtil(String indexDirLocation, String projectDirLocation) throws IOException {
        this.indexDirLocation = indexDirLocation;
        this.projectDirLocation = projectDirLocation;
        indexDir = FSDirectory.open(Paths.get(indexDirLocation));
        sort = new Sort(new SortField(FIELD_ID, Type.STRING));
    }

    private void loadIndexReader() throws IOException {
        reader = DirectoryReader.open(indexDir);
        searcher = new IndexSearcher(reader);

        if (queryParser == null) {
            queryParser = new QueryBuilder(new StandardAnalyzer());
        }
    }

    private void loadIndexWriter() throws IOException {
        indexWriterConfig = new IndexWriterConfig().setIndexSort(sort);
        writer = new IndexWriter(indexDir, indexWriterConfig);
    }

    public static EntityIndexingUtil getInstance(ProjectEntity project) throws IOException {
        String indexDirLocation = LUCENE_DIR_LOCATION + project.getUUID();
        if (instance == null || !StringUtils.equals(indexDirLocation, instance.getIndexDirLocation())) {
            instance = new EntityIndexingUtil(indexDirLocation, project.getFolderLocation());
        }
        return instance;
    }

    public String getIndexDirLocation() {
        return indexDirLocation;
    }

    public List<String> getIndexedEntityIds(String entityExtension) {
        try {
            loadIndexReader();
            TopDocs topdocs = search(entityExtension);
            if (topdocs == null) {
                return Collections.emptyList();
            }

            ScoreDoc[] scoreDocs = topdocs.scoreDocs;
            if (scoreDocs == null) {
                return Collections.emptyList();
            }

            List<String> ids = Arrays.asList(scoreDocs)
                    .parallelStream()
                    .filter(scoreDoc -> isValidScoreDoc(scoreDoc))
                    .map(scoreDoc -> getIndexedEntityID(scoreDoc))
                    .filter(id -> id != null)
                    .sorted()
                    .collect(Collectors.toList());
            return ids;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return Collections.emptyList();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    private boolean isValidScoreDoc(ScoreDoc scoreDoc) {
        try {
            return scoreDoc != null && searcher.doc(scoreDoc.doc) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private String getIndexedEntityID(ScoreDoc scoreDoc) {
        try {
            return searcher.doc(scoreDoc.doc).get(FIELD_ID);
        } catch (IOException e) {
            return null;
        }
    }

    public int doIndex() {
        try {
            loadIndexWriter();
            writer.deleteAll();
            indexDir(projectDirLocation, new FileFilter() {

                @Override
                public boolean accept(File file) {
                    String name = file.getName().toLowerCase();
                    return name.endsWith(TestCaseEntity.getTestCaseFileExtension())
                            || name.endsWith(TestSuiteEntity.getTestSuiteFileExtension())
                            || name.endsWith(CheckpointEntity.getCheckpointFileExtension())
                            || name.endsWith(DataFileEntity.getTestDataFileExtension())
                            || name.endsWith(WebElementEntity.getWebElementFileExtension());
                }
            });

            return writer.numDocs();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return -1;
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    private void indexDir(String dataDirPath, FileFilter filter) {
        Arrays.asList(new File(dataDirPath).listFiles())
                .parallelStream()
                .filter(item -> !item.isHidden() && item.exists() && item.canRead())
                .forEach(item -> {
                    if (item.isDirectory()) {
                        indexDir(item.getAbsolutePath(), filter);
                        return;
                    }

                    if (filter.accept(item)) {
                        indexFile(item);
                    }
                });
    }

    private void indexFile(File file) {
        try {
            writer.addDocument(createIndexDocument(file));
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private Document createIndexDocument(File file) {
        Document document = new Document();

        document.add(new Field(FIELD_ID, getEntityID(file), TextField.TYPE_STORED));
        document.add(new Field(FIELD_TYPE, FilenameUtils.getExtension(file.getName()), TextField.TYPE_STORED));

        return document;
    }

    /**
     * Get Katalon Entity ID from physical file path
     * 
     * @param file File
     * @return Katalon Entity ID
     */
    private String getEntityID(File file) {
        // remove extension
        String id = FilenameUtils.removeExtension(file.getAbsolutePath());
        // get relative path to project location
        id = StringUtils.removeStart(id,
                ProjectController.getInstance().getCurrentProject().getFolderLocation() + File.separator);
        // convert relative path to Katalon entity ID
        id = FilenameUtils.separatorsToUnix(id);
        return id;
    }

    private TopDocs search(String searchQuery) throws IOException {
        Query query = queryParser.createBooleanQuery(FIELD_TYPE, searchQuery);
        int maxDoc = reader.maxDoc();
        if (maxDoc <= 0) {
            return null;
        }
        return searcher.search(query, maxDoc); // get all result
    }

}
