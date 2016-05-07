package com.kms.katalon.composer.util.groovy;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.text.edits.TextEdit;

@SuppressWarnings("restriction")
public class ImportsManager {

	private final CompilationUnit fAstRoot;
	private final ImportRewrite fImportsRewrite;

	public ImportsManager(CompilationUnit astRoot) {
		fAstRoot= astRoot;
		fImportsRewrite= StubUtility.createImportRewrite(astRoot, true);
	}

	ICompilationUnit getCompilationUnit() {
		return fImportsRewrite.getCompilationUnit();
	}
	
	public String addImport(String qualifiedTypeName) {
		return fImportsRewrite.addImport(qualifiedTypeName);
	}
	
	public String addImport(ITypeBinding typeBinding) {
		return fImportsRewrite.addImport(typeBinding);
	}
	
	public String addStaticImport(String declaringTypeName, String simpleName, boolean isField) {
		return fImportsRewrite.addStaticImport(declaringTypeName, simpleName, isField);
	}

	public void create(boolean needsSave, IProgressMonitor monitor) throws CoreException {
		TextEdit edit= fImportsRewrite.rewriteImports(monitor);
		JavaModelUtil.applyEdit(fImportsRewrite.getCompilationUnit(), edit, needsSave, null);
	}

	public void removeImport(String qualifiedName) {
		fImportsRewrite.removeImport(qualifiedName);
	}

	public void removeStaticImport(String qualifiedName) {
		fImportsRewrite.removeStaticImport(qualifiedName);
	}
}