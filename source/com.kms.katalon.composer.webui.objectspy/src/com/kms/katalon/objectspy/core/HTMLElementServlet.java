package com.kms.katalon.objectspy.core;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.w3c.dom.Document;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLRawElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

@SuppressWarnings("restriction")
public class HTMLElementServlet extends HttpServlet {
	private static final String ELEMENT_KEY = "element";
	private static final String ELEMENT_MAP_KEY = "elementsMap";
	private static final long serialVersionUID = 1L;
	private Logger logger;
	private IEventBroker eventBroker;

	public HTMLElementServlet(Logger logger, IEventBroker eventBroker) {
		this.logger = logger;
		this.eventBroker = eventBroker;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		if (sb.indexOf("=") != -1) {
			String key = HTMLElementUtil.decodeURIComponent(sb.substring(0, sb.indexOf("=")));
			if (key.equals(ELEMENT_KEY)) {
				String value = sb.substring(sb.indexOf("=") + 1, sb.length());
				HTMLElement newElement = null;
				try {
					newElement = HTMLElementUtil.buildHTMLElement(value);
				} catch (Exception e) {
					logger.error(e);
				}
				response.setContentType("text/html");
				response.addHeader("Access-Control-Allow-Origin", "*");
				if (newElement != null) {
					eventBroker.post(EventConstants.OBJECT_SPY_ELEMENT_ADDED, newElement);
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else if (key.equals(ELEMENT_MAP_KEY)) {
				final String value = sb.substring(sb.indexOf("=") + 1, sb.length());

				response.setContentType("text/html");
				response.addHeader("Access-Control-Allow-Origin", "*");
				response.setStatus(HttpServletResponse.SC_OK);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						Document htmlDocument = null;
						HTMLRawElement newRootElement = null;
						try {
							DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

							// root elements
							htmlDocument = docBuilder.newDocument();
							newRootElement = HTMLElementUtil.buildHTMLRawElement(htmlDocument, value);
						} catch (Exception e) {
							logger.error(e);
						}
						if (htmlDocument != null && newRootElement != null) {
							eventBroker.post(EventConstants.OBJECT_SPY_ELEMENT_DOM_MAP_ADDED, new Object[] { htmlDocument,
									newRootElement });
						}
					}
				}).run();
			}
		}
	}
}