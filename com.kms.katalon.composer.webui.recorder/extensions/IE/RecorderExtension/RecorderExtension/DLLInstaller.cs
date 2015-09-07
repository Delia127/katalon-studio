using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration.Install;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows.Forms;

namespace RecorderExtension
{
    [RunInstaller(true)]
    [Guid("EC55F88B-1028-4F23-833C-17F0E0CA0D59")]
    public partial class DLLInstaller : System.Configuration.Install.Installer
    {
        static bool is64BitProcess = (IntPtr.Size == 8);
        static bool is64BitOperatingSystem = is64BitProcess || InternalCheckIsWow64();
        static string ieVersion;

        [DllImport("kernel32.dll", SetLastError = true, CallingConvention = CallingConvention.Winapi)]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool IsWow64Process(
            [In] IntPtr hProcess,
            [Out] out bool wow64Process
        );

        public static bool InternalCheckIsWow64()
        {
            if ((Environment.OSVersion.Version.Major == 5 && Environment.OSVersion.Version.Minor >= 1) ||
                Environment.OSVersion.Version.Major >= 6)
            {
                using (Process p = Process.GetCurrentProcess())
                {
                    bool retVal;
                    if (!IsWow64Process(p.Handle, out retVal))
                    {
                        return false;
                    }
                    return retVal;
                }
            }
            else
            {
                return false;
            }
        }

        static void getIEVersion()
        {
            string version = new WebBrowser().Version.ToString();
            ieVersion = version.Substring(0, version.IndexOf('.'));
        }

        public DLLInstaller()
        {
            InitializeComponent();
        }

        [System.Security.Permissions.SecurityPermission(System.Security.Permissions.SecurityAction.Demand)]
        public override void Commit(System.Collections.IDictionary savedState)
        {
            base.Commit(savedState);
        }

        [System.Security.Permissions.SecurityPermission(System.Security.Permissions.SecurityAction.Demand)]
        public override void Install(System.Collections.IDictionary stateSaver)
        {
            base.Install(stateSaver);
            register();
        }

        [System.Security.Permissions.SecurityPermission(System.Security.Permissions.SecurityAction.Demand)]
        public override void Uninstall(System.Collections.IDictionary stateSaver)
        {
            base.Uninstall(stateSaver);
            unregister();
        }

        private void executeRegasm(bool unregister) {

            // Get IE Version
            var t = new Thread(getIEVersion);
            t.SetApartmentState(ApartmentState.STA);
            t.Start();
            t.Join();

            // Get the location of regasm
            string regasmPath = System.Runtime.InteropServices.RuntimeEnvironment.GetRuntimeDirectory() + @"regasm.exe";
            // Get the location of our DLL
            string componentPath = typeof(DLLInstaller).Assembly.Location;

            string executeCommand = (unregister ? "/unregister " : "") + "\"" + componentPath + "\"";
            //if (ieVersion == "11")
            //{
            //    System.Diagnostics.Process.Start(regasmPath, executeCommand).WaitForExit();
            //    //if (!is64BitOperatingSystem)
            //    //{
            //    //    System.Diagnostics.Process.Start(regasmPath, executeCommand).WaitForExit();
            //    //}
            //    //else
            //    //{
            //    //    string regasm64Path = regasmPath.Replace("Framework", "Framework64");
            //    //    System.Diagnostics.Process.Start(regasm64Path, executeCommand).WaitForExit();
            //    //}
            //}
            //else
            //{
                // Execute regasm
                System.Diagnostics.Process.Start(regasmPath, executeCommand).WaitForExit();
                if (is64BitOperatingSystem)
                {
                    string regasm64Path = regasmPath.Replace("Framework", "Framework64");
                    System.Diagnostics.Process.Start(regasm64Path, executeCommand).WaitForExit();
                }
            //}
        }

        private void register()
        {
            executeRegasm(false);
        }

        private void unregister()
        {
            executeRegasm(true);
        }
    }
}
