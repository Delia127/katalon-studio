using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;

namespace ObjectSpyExtension
{
    [ComVisible(true),
     Guid("4C1D2E51-018B-4A7C-8A07-618452573E42"),
     InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IHttpRequestExtension {
        [DispId(1)]
        String postRequest(String data, String url);
        [DispId(2)]
        String sendRequestToKatalon(String data, String url);
    }
}
