//
//  WebViewerController.swift
//  TestApp
//
//  Created by Hieu Mai on 12/15/15.
//  Copyright (c) 2015 KMS. All rights reserved.
//

import UIKit

class WebViewerController: UIViewController {
    
    @IBOutlet weak var webView: UIWebView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        let url = URL (string: "https://www.google.com");
        let requestObj = URLRequest(url: url!);
        webView.loadRequest(requestObj);
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func goToListViewer(_ sender : AnyObject) {
    }
    
    @IBAction func goToWebViewer(_ sender : AnyObject) {
    }
}
