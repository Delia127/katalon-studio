//
//  FormViewController.swift
//  TestApp
//
//  Created by Hieu Mai on 6/10/16.
//  Copyright © 2016 KMS. All rights reserved.
//

import UIKit

@available(iOS 8.0, *)
class FormViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
    
    @IBOutlet weak var button: UIButton!
    
    @IBOutlet weak var browseButton: UIButton!
    
    @IBOutlet weak var cameraButton: UIButton!
    
    @IBOutlet weak var imageView: UIImageView!
    
    var imagePicker = UIImagePickerController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

    @IBAction func buttonTapped(_ sender: AnyObject) {
        if sender === button {
            let alertController = UIAlertController(title: "iOScreator", message:
                "Hello, world!", preferredStyle: UIAlertControllerStyle.alert)
            alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default,handler: nil))
            
            self.present(alertController, animated: true, completion: nil)
        } else if sender === browseButton {
            if UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum){
                print("Button browse")
                
                imagePicker.delegate = self
                imagePicker.sourceType = .savedPhotosAlbum;
                imagePicker.allowsEditing = false
                
                self.present(imagePicker, animated: true, completion: nil)
            }
        } else if sender === cameraButton {
            print("Button camera")
            
            imagePicker.delegate = self
            imagePicker.sourceType = .camera
            
            self.present(imagePicker, animated: true, completion: nil)
        }
        
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        imageView.image = info[UIImagePickerControllerOriginalImage] as? UIImage
        picker.dismiss(animated: true, completion: nil)
        
    }
}
