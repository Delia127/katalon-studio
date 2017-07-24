//
//  TableViewController.swift
//  TestApp
//
//  Created by Hieu Mai on 12/15/15.
//  Copyright (c) 2015 KMS. All rights reserved.
//

import UIKit

class TableViewController : UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        tableView.dataSource = self
        tableView.delegate = self
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 100
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) ->   UITableViewCell {
        let cell = UITableViewCell()
        
        let button = UIButton(frame: CGRect(x:0, y:0, width:200, height:50))
        button.setTitle("Button \(indexPath.row)", for: UIControlState())
        button.setTitleColor(UIColor.black, for: UIControlState())
        button.addTarget(self, action: #selector(TableViewController.buttonTapAction(_:)), for: UIControlEvents.touchUpInside)
        
        cell.addSubview(button)
        return cell
    }
    
    
    // UITableViewDelegate Functions
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 100
    }
    
    func buttonTapAction(_ sender : UIButton!)
    {
        sender.setTitle("Selected", for: UIControlState())
        sender.setTitleColor(UIColor.red, for: UIControlState())
    }
}
