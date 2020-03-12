package com.kms.katalon.composer.webui.execution.menu.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;

import com.kms.katalon.composer.webui.execution.menu.EdgeChromiumExecutionDynamicContribution;

public class EdgeChromiumExecutionDynamicContributionTest {

	private EdgeChromiumExecutionDynamicContribution contribution = new EdgeChromiumExecutionDynamicContribution();
	/*EdgeChromiumExecutionDynamicContribution contribution;
	
	@Test
	public void aboutToShowTest() throws Exception{
		
		Bundle bundle = Platform.getBundle("com.kms.katalon.composer.webui");
		PlatformUI.getWorkbench().
		BundleContext context = bundle.getBundleContext();
		ServiceReference<ECommandService> serviceReference = bundle.getBundleContext().getServiceReference(ECommandService.class);
		ECommandService service = bundle.getBundleContext().getService(serviceReference);
		
		contribution = new EdgeChromiumExecutionDynamicContribution();
//		MockitoAnnotations.initMocks(this);
		
		List<MMenuElement> items = new ArrayList<>();
		//items.add(MenuFactory.createPopupMenuItem(null, null, null) );
		System.out.println("Is Empty: " + items.isEmpty()); 
		contribution.aboutToShow(items);
		System.out.println(items.toString());
		assertEquals(false, items.isEmpty());
	}*/
	
	@Test
	public void getIconUriTest() throws Exception{		
		Method method = contribution.getClass().getDeclaredMethod("getIconUri");
		method.setAccessible(true);
		//System.out.println(method.getParameterTypes().toString() + "-" + method.getParameterCount());
		String result = (String) method.invoke(contribution);
		String expected = "/icons/execution/edge_chromium_16.png";
		assertEquals(result.endsWith(expected), true);
	}
	
	@Test
	public void getDriverTypeNameTest() throws Exception{		
		Method method = contribution.getClass().getDeclaredMethod("getDriverTypeName");
		method.setAccessible(true);
		String result = (String) method.invoke(contribution);
		String expected = "Edge Chromium";
		assertEquals(expected, result);
	}
	
	@Test
	public void getCommandIdTest() throws Exception{
		Method method = contribution.getClass().getDeclaredMethod("getCommandId");
		method.setAccessible(true);
		String result = (String) method.invoke(contribution);
		String expected = "com.kms.katalon.composer.webui.execution.command.edge.chromium";
		assertEquals(expected, result);
	}
	
	@Test
	public void getMenuLabelTest() throws Exception{
		Method method = contribution.getClass().getDeclaredMethod("getMenuLabel");
		method.setAccessible(true);
		String result = (String) method.invoke(contribution);
		String expected = "Edge Chromium";
		assertEquals(expected, result);
	}
}
