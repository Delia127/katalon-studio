package com.kms.katalon.composer.explorer.providers;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;

@SuppressWarnings("restriction")
public class EntityProvider implements ITreeContentProvider{

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List) {
			return ((List<Object>)inputElement).toArray();
		} else if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		return Collections.emptyList().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		try {
			if (parentElement instanceof ITreeEntity) {
				return ((ITreeEntity) parentElement).getChildren();
			} 
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}
		return Collections.emptyList().toArray();
	}

	@Override
	public Object getParent(Object element) {
		try {
			if (element instanceof ITreeEntity) {
				return ((ITreeEntity) element).getParent();
			} 
		} catch (Exception e) {
		    LoggerSingleton.getInstance().getLogger().error(e);
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		try {
			if (element instanceof ITreeEntity) {
				return ((ITreeEntity) element).hasChildren();
			}
		} catch (Exception e) {
		    LoggerSingleton.getInstance().getLogger().error(e);
		}
		return false;
	}
	
	public TreePath getTreePath(Object element) {
	    Object parentElement = getParent(element);
	    if (parentElement != null) {
	        return getTreePath(parentElement).createChildPath(element);
	    } else {
	        return new TreePath(new Object[] {element});
	    }
	}
	
	@SuppressWarnings("unchecked")
    public void collectDifferentTypeEntities(Object element, List<Class<ITreeEntity>> clazzes, List<ITreeEntity> entities) {
	    if (hasChildren(element)) {
            for (Object child : getChildren(element)) {
                collectDifferentTypeEntities(child, clazzes, entities);
            }
        } else {
            try {
                if (!clazzes.contains(element.getClass())) {
                    clazzes.add((Class<ITreeEntity>) element.getClass());
                    entities.add((ITreeEntity) element);
                }
            } catch (Exception e) {
                LoggerSingleton.getInstance().getLogger().error(e);
            }           
        }
	}
}
