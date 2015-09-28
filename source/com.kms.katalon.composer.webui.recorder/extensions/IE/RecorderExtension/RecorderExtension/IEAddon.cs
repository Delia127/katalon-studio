using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using Microsoft.Win32;
using mshtml;
using SHDocVw;
using System.IO;
using System.Runtime.InteropServices.Expando;
using System.Reflection;
using System.Net;
using System.Collections.Specialized;
using System.Text;

namespace RecorderExtension
{
    [ComVisible(true),
     ClassInterface(ClassInterfaceType.None), 
     ProgId("KMS.qAutomate.RecorderBHO.Recorder"),
     Guid("FEA8CA38-7979-4F6A-83E4-2949EDEA96EF"),
     ComDefaultInterface(typeof(IHttpRequestExtension))]
    public class ObjectSpyBHO : IObjectWithSite, IHttpRequestExtension
    {
        private object site;
        private IWebBrowser2 browser;

        private static Guid guidIWebBrowserApp = Marshal.GenerateGuidForType(typeof(IWebBrowserApp)); 
        private static Guid guidIWebBrowser2 = Marshal.GenerateGuidForType(typeof(IWebBrowser2));
        private static string serverUrl;
        private static String windowHandle;

        private static string AssemblyDirectory
        {
            get
            {
                UriBuilder uri = new UriBuilder(Assembly.GetExecutingAssembly().CodeBase);
                string path = Uri.UnescapeDataString(uri.Path);
                return Path.GetDirectoryName(path);
            }
        }

        #region Handle Events
        private void OnDocumentComplete(object pDisp, ref object URL)
        {
            try
            {
                if (pDisp != this.site)
                {
                    IWebBrowser2 childBrowser = getBrowser(pDisp);
                    if (childBrowser != null)
                    {
                        IHTMLDocument2 document2 = childBrowser.Document as IHTMLDocument2;
                        IHTMLWindow2 window = document2.parentWindow;
                        runScriptOnWindow(window, this);
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.InnerException.StackTrace);
            }
            
        }

        private void OnDownloadComplete()
        {
            IHTMLDocument2 doc = browser.Document as IHTMLDocument2;
            if (doc != null)
            {
                IHTMLWindow2 tmpWindow = doc.parentWindow;
                if (tmpWindow != null)
                {
                    HTMLWindowEvents2_Event events = (tmpWindow as HTMLWindowEvents2_Event);
                    try
                    {
                        events.onload -= new HTMLWindowEvents2_onloadEventHandler(RefreshHandler);
                    }
                    catch (Exception ex) {
                        MessageBox.Show(ex.InnerException.Message);
                    }
                    events.onload += new HTMLWindowEvents2_onloadEventHandler(RefreshHandler);
                }
            }
        }

        public void RefreshHandler(IHTMLEventObj e)
        {
            try
            {
                IHTMLDocument2 document2 = browser.Document as IHTMLDocument2;
                IHTMLWindow2 window = document2.parentWindow;
                runScriptOnWindow(window, this);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private static void runScriptOnWindow(dynamic window, IHttpRequestExtension extensionClass)
        {
            IExpando windowEx = (IExpando)window;

            PropertyInfo propertyInfo = windowEx.GetProperty("httpRequestExtension", System.Reflection.BindingFlags.IgnoreCase);
            if (propertyInfo == null)
            {
                propertyInfo = windowEx.AddProperty("httpRequestExtension");
            }
            propertyInfo.SetValue(windowEx, extensionClass, null);

            window.execScript(Properties.Resources.jquery_1_11_2_min);
            window.execScript(Properties.Resources.json3_min);
            window.execScript("windowId = '" + windowHandle + "';");
            window.execScript("qAutomate_server_url = '" + serverUrl + "';");
            window.execScript(Properties.Resources.common);
            window.execScript(Properties.Resources.dom_recorder);
        }

        #endregion

        [Guid("6D5140C1-7436-11CE-8034-00AA006009FA")]
        [InterfaceType(1)]
        public interface IServiceProvider
        {
            int QueryService(ref Guid guidService, ref Guid riid, out IntPtr ppvObject);
        }

        #region Implementation of IObjectWithSite
        private IWebBrowser2 getBrowser(object site)
        {
            if (site != null && site is IServiceProvider)
            {
                var serviceProv = (IServiceProvider)site;
                IntPtr intPtr;
                serviceProv.QueryService(ref guidIWebBrowserApp, ref guidIWebBrowser2, out intPtr);
                object result = Marshal.GetObjectForIUnknown(intPtr);
                if (result is IWebBrowser2)
                {
                    return (IWebBrowser2)result;
                }
            }
            return null;
        }

        int IObjectWithSite.SetSite(object site)
        {
            if (site != null)
            {
                string userSettingFolderLocation = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "KMS", "qAutomate", "Recorder");
                if (Directory.Exists(userSettingFolderLocation))
                {
                    string serverSettingFile = Path.Combine(userSettingFolderLocation, "serverUrl.txt");
                    if (File.Exists(serverSettingFile))
                    {
                        serverUrl = File.ReadAllText(serverSettingFile);
                        this.site = site;
                        //var guidIWebBrowserApp = Marshal.GenerateGuidForType(typeof(IWebBrowserApp)); // new Guid("0002DF05-0000-0000-C000-000000000046");
                        //var guidIWebBrowser2 = Marshal.GenerateGuidForType(typeof(IWebBrowser2)); // new Guid("D30C1661-CDAF-11D0-8A3E-00C04FC9E26E");

                        browser = getBrowser(site);
                        windowHandle = Guid.NewGuid().ToString();
                        ((DWebBrowserEvents2_Event)browser).DocumentComplete +=
                            new DWebBrowserEvents2_DocumentCompleteEventHandler(this.OnDocumentComplete);
                        ((DWebBrowserEvents2_Event)browser).DownloadComplete +=
                            new DWebBrowserEvents2_DownloadCompleteEventHandler(this.OnDownloadComplete);
                    }
                }
            }
            else
            {
                ((DWebBrowserEvents2_Event)browser).DocumentComplete -=
                    new DWebBrowserEvents2_DocumentCompleteEventHandler(this.OnDocumentComplete);
                ((DWebBrowserEvents2_Event)browser).DownloadComplete -=
                    new DWebBrowserEvents2_DownloadCompleteEventHandler(this.OnDownloadComplete);
                browser = null;
            }
            return 0;
        }

        int IObjectWithSite.GetSite(ref Guid guid, out IntPtr ppvSite)
        {
            IntPtr punk = Marshal.GetIUnknownForObject(browser);
            int hr = Marshal.QueryInterface(punk, ref guid, out ppvSite);
            Marshal.Release(punk);
            return hr;
        }
        #endregion

        #region Registering with regasm
        public static string RegBHO = "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Browser Helper Objects";

        [ComRegisterFunction]
        public static void RegisterBHO(Type type)
        {
            string guid = type.GUID.ToString("B");

            // Register BHO
            {
                RegistryKey registryKey = Registry.LocalMachine.OpenSubKey(RegBHO, true);
                if (registryKey == null)
                    registryKey = Registry.LocalMachine.CreateSubKey(RegBHO);
                RegistryKey key = registryKey.OpenSubKey(guid);
                if (key == null)
                    key = registryKey.CreateSubKey(guid);
                key.SetValue("Alright", 1);
                //key.SetValue("NoExplorer", 1, RegistryValueKind.DWord);
                registryKey.Close();
                key.Close();
            }
        }

        [ComUnregisterFunction]
        public static void UnregisterBHO(Type type)
        {
            string guid = type.GUID.ToString("B");
            // BHO
            {
                RegistryKey registryKey = Registry.LocalMachine.OpenSubKey(RegBHO, true);
                if (registryKey != null)
                    registryKey.DeleteSubKey(guid, false);
            }
        }
        #endregion

        public String postRequest(string data, string url)
        {
            using (var client = new WebClient())
            {
                try
                {
                    client.UploadString(url, data);
                    return "200";
                }
                catch (WebException webException)
                {
                    if (webException.Response is HttpWebResponse)
                    {
                        HttpWebResponse response = (HttpWebResponse) webException.Response;
                        return "HTTP Status Code: " + (int)(response.StatusCode);
                    }
                    return "Extension error: " + webException.Message;
                }
            }
        }
    }
}