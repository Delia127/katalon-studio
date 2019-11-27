package com.kms.katalon.composer.execution.constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
public class ProblemMarkerConstants {
    public static List<IMarker> findErrorMarkers(IProject project) throws CoreException {
        return findMarkers(project, IMarker.SEVERITY_ERROR);
      }

    public static List<IMarker> findMarkers(IProject project, int targetSeverity) throws CoreException {
        return findMarkers(project, targetSeverity, null /*withAttribute*/);
      }

    private static List<IMarker> findMarkers(IProject project, int targetSeverity, String withAttribute)
            throws CoreException {
        SortedMap<IMarker, IMarker> errors = new TreeMap<IMarker, IMarker>(new Comparator<IMarker>() {
            public int compare(IMarker o1, IMarker o2) {
                int lineNumber1 = o1.getAttribute(IMarker.LINE_NUMBER, -1);
                int lineNumber2 = o2.getAttribute(IMarker.LINE_NUMBER, -1);
                if (lineNumber1 < lineNumber2) {
                    return -1;
                }
                if (lineNumber1 > lineNumber2) {
                    return 1;
                }
                // Markers on the same line
                String message1 = o1.getAttribute(IMarker.MESSAGE, "");
                String message2 = o2.getAttribute(IMarker.MESSAGE, "");
                return message1.compareTo(message2);
            }
        });
        for (IMarker marker : project.findMarkers(null /* all markers */, true /* subtypes */,
                IResource.DEPTH_INFINITE)) {
            int severity = marker.getAttribute(IMarker.SEVERITY, 0);
            if (severity != targetSeverity) {
                continue;
            }
            if (withAttribute != null) {
                String attribute = marker.getAttribute(withAttribute, null);
                if (attribute == null) {
                    continue;
                }
            }
            errors.put(marker, marker);
        }
        List<IMarker> result = new ArrayList<IMarker>();
        result.addAll(errors.keySet());
        return result;
        }
}
