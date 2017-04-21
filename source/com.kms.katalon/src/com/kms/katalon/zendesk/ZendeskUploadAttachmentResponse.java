package com.kms.katalon.zendesk;

public class ZendeskUploadAttachmentResponse {
    private ZendeskAttachmentResponseUpload upload;

    public ZendeskAttachmentResponseUpload getUpload() {
        return upload;
    }

    public void setUpload(ZendeskAttachmentResponseUpload upload) {
        this.upload = upload;
    }

    public class ZendeskAttachmentResponseUpload {
        private String token;

        /**
         * Uploaded attachment
         */
        private ZendeskAttachment attachment;

        /**
         * All the attachments in the current uploading process
         * See more https://developer.zendesk.com/rest_api/docs/core/attachments#upload-files
         */
        private ZendeskAttachment[] attachments;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public ZendeskAttachment[] getAttachments() {
            return attachments;
        }

        public void setAttachments(ZendeskAttachment[] attachments) {
            this.attachments = attachments;
        }

        public ZendeskAttachment getAttachment() {
            return attachment;
        }

        public void setAttachment(ZendeskAttachment attachment) {
            this.attachment = attachment;
        }
    }
}
