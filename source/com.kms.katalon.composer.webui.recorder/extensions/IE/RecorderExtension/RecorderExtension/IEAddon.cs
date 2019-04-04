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
using System.Diagnostics;

namespace RecorderExtension
{
    [ComVisible(true),
     ClassInterface(ClassInterfaceType.None), 
     ProgId("KMS.qAutomate.RecorderBHO.Recorder"),
     Guid("FEA8CA38-7979-4F6A-83E4-2949EDEA96EF"),
     ComDefaultInterface(typeof(IHttpRequestExtension))]
    public class RecorderBHO : IObjectWithSite, IHttpRequestExtension
    {
        private object site;
        private IWebBrowser2 browser;
        private static string serverUrl;
        private static String windowHandle;
        private static String addonDataFolder = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "KMS", "qAutomate", "Recorder");

        #region Handle Document Events

        private void OnDocumentComplete(object pDisp, ref object URL)
        {
            OnDocumentComplete();
        }

        private void OnDocumentComplete()
        {
            try
            {
                serverUrl = GetKatalonServerUrl();
                if (serverUrl != null)
                {
                    RunScriptOnWindow();
                }
            }
            catch (Exception ex)
            {
                logError(ex);
            }
        }

        private void RunScriptOnWindow()
        {

            HTMLDocument document = (HTMLDocument)browser.Document;
            dynamic window = document.parentWindow;
            IExpando ScriptObject = (IExpando)window;
            PropertyInfo propertyInfo = ScriptObject.GetProperty("httpRequestExtension", BindingFlags.Default);
            if (propertyInfo == null)
            {
                propertyInfo = ScriptObject.AddProperty("httpRequestExtension");
            }
            propertyInfo.SetValue(ScriptObject, this, null);

            serverUrl = "http://localhost:57080/";

            window.execScript(Properties.Resources.jquery_1_11_2_min);
            window.execScript(Properties.Resources.json3_min);
            window.execScript("windowId = '" + windowHandle + "';");
            window.execScript("qAutomate_server_url = '" + serverUrl + "';");
            window.execScript(Properties.Resources.constants);
            window.execScript(Properties.Resources.common);
            window.execScript(Properties.Resources.record_common);
            window.execScript(Properties.Resources.dom_recorder);
            window.execScript(Properties.Resources.main);
        }

        #endregion

        [Guid("6D5140C1-7436-11CE-8034-00AA006009FA")]
        [InterfaceType(1)]
        public interface IServiceProvider
        {
            int QueryService(ref Guid guidService, ref Guid riid, out IntPtr ppvObject);
        }

        #region Implementation of IObjectWithSite
        private IWebBrowser2 GetBrowser(object site)
        {
            if (site == null || !(site is IServiceProvider))
            {
                return null;
            }
            var serviceProv = (IServiceProvider)site;
            IntPtr intPtr;
            Guid guidIWebBrowserApp = Marshal.GenerateGuidForType(typeof(IWebBrowserApp));
            Guid guidIWebBrowser2 = Marshal.GenerateGuidForType(typeof(IWebBrowser2));
            serviceProv.QueryService(ref guidIWebBrowserApp, ref guidIWebBrowser2, out intPtr);
            object result = Marshal.GetObjectForIUnknown(intPtr);
            if (result is IWebBrowser2)
            {
                return (IWebBrowser2)result;
            }
            return null;
        }

        private string GetKatalonServerUrl()
        {
            Stopwatch sw = new Stopwatch();
            sw.Start();
            string serverSettingFile;
            while (true)
            {
                if (!Directory.Exists(addonDataFolder))
                {
                    return null;
                }
                serverSettingFile = Path.Combine(addonDataFolder, "serverUrl.txt");
                if (!File.Exists(serverSettingFile))
                {
                    return null;
                }
                string serverUrl = File.ReadAllText(serverSettingFile);
                try
                {
                    WebRequest request = WebRequest.Create(serverUrl);
                    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                    if (response.StatusCode == HttpStatusCode.OK)
                    {
                        return serverUrl;
                    }
                }
                catch (Exception ex)
                {
                    // server not available at all, for some reason
                }
                if (sw.ElapsedMilliseconds > 5000)
                {
                    break;
                };
            }
            if (serverSettingFile != null) {
                File.Delete(serverSettingFile);
            }
            return null;
        }

        private void logError(Exception e)
        {
            if (e == null)
            {
                return;
            }
            try
            {
                if (!Directory.Exists(addonDataFolder))
                {
                    Directory.CreateDirectory(addonDataFolder);
                }
                long currentMilis = DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;
                string newLogFile = Path.Combine(addonDataFolder, currentMilis.ToString() + ".log");
                File.WriteAllText(newLogFile, e.ToString());
            }
            catch (Exception)
            {
                // IO Exceptions, ignored
            }

        }

        private IWebBrowser2 getBrowser(object site)
        {
            if (site != null && site is IServiceProvider)
            {
                var serviceProv = (IServiceProvider)site;
                IntPtr intPtr;
                Guid guidIWebBrowserApp = Marshal.GenerateGuidForType(typeof(IWebBrowserApp)); 
                Guid guidIWebBrowser2 = Marshal.GenerateGuidForType(typeof(IWebBrowser2));
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
            if (site == null)
            {
                Dispose();
                browser = null;
                windowHandle = null;
                return 0;
            }
            try
            {
                this.site = site;
                browser = GetBrowser(site);
                windowHandle = Guid.NewGuid().ToString();
                Setup();
            }
            catch (Exception ex)
            {
                logError(ex);
            }
            return 0;
        }

        private void Dispose()
        {
            ((DWebBrowserEvents2_Event)browser).DocumentComplete -=
                new DWebBrowserEvents2_DocumentCompleteEventHandler(this.OnDocumentComplete);
            ((DWebBrowserEvents2_Event)browser).DownloadComplete -=
                new DWebBrowserEvents2_DownloadCompleteEventHandler(this.OnDocumentComplete);
            ((DWebBrowserEvents2_Event)browser).NavigateComplete2 -=
                new DWebBrowserEvents2_NavigateComplete2EventHandler(this.OnDocumentComplete);
        }

        private void Setup()
        {
            ((DWebBrowserEvents2_Event)browser).DocumentComplete +=
                new DWebBrowserEvents2_DocumentCompleteEventHandler(this.OnDocumentComplete);
            ((DWebBrowserEvents2_Event)browser).DownloadComplete +=
                new DWebBrowserEvents2_DownloadCompleteEventHandler(this.OnDocumentComplete);
            ((DWebBrowserEvents2_Event)browser).NavigateComplete2 +=
                new DWebBrowserEvents2_NavigateComplete2EventHandler(this.OnDocumentComplete);
        }

        int IObjectWithSite.GetSite(ref Guid guid, out IntPtr ppvSite)
        {
            try
            {
                IntPtr punk = Marshal.GetIUnknownForObject(browser);
                int hr = Marshal.QueryInterface(punk, ref guid, out ppvSite);
                Marshal.Release(punk);
                return hr;
            }
            catch (Exception ex)
            {
                logError(ex);
                throw ex;
            }
        }
        #endregion

        #region Registering with regasm
        public static string RegBHO = "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Browser Helper Objects";

        [ComRegisterFunction]
        public static void RegisterBHO(Type type)
        {
            string guid = type.GUID.ToString("B");
            RegistryKey registryKey = Registry.LocalMachine.OpenSubKey(RegBHO, true);
            if (registryKey == null)
            {
                registryKey = Registry.LocalMachine.CreateSubKey(RegBHO);
            }
            RegistryKey key = registryKey.OpenSubKey(guid);
            if (key == null)
            {
                key = registryKey.CreateSubKey(guid);
            }
            key.SetValue("Alright", 1);
            registryKey.Close();
            key.Close();
        }

        [ComUnregisterFunction]
        public static void UnregisterBHO(Type type)
        {
            string guid = type.GUID.ToString("B");
            RegistryKey registryKey = Registry.LocalMachine.OpenSubKey(RegBHO, true);
            if (registryKey != null)
            {
                registryKey.DeleteSubKey(guid, false);
            }
        }
        #endregion

        #region Handle web request
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
                        HttpWebResponse response = (HttpWebResponse)webException.Response;
                        return "HTTP Status Code: " + (int)(response.StatusCode);
                    }
                    if (webException.Message.Contains("Unable to connect to the remote server"))
                    {
                        return "Cannot connect to Katalon Server. Make sure you have started Recorder on Katalon application.";
                    }
                    return "Web Exception Error: " + webException.Message;
                }
                catch (Exception ex)
                {
                    logError(ex);
                    return "Internal Error: " + ex.ToString(); ;
                }
            }
        }
        #endregion
    }
}