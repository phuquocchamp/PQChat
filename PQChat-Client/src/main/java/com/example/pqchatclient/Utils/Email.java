package com.example.pqchatclient.Utils;

import java.util.Date;
import java.util.Properties;
//import javax.
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {
    // Email : pqchattingapp@gmail.com
    // password : vezumuxdvxbrzxxe
    static final String sendFrom = "pqchattingapp@gmail.com";
    static final String password = "vezumuxdvxbrzxxe";


    public static boolean sendEmail(String sendTo, String title, String validationCode){
        // Properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); // TLS 587 SSL 465
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Create Authenticator
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sendFrom, password);
            }
        };

        // Message Sample
        String messageSample =
                "<!doctype html>\n" +
                "<html lang=\"en-US\">\n" +
                "\n" +
                "    <head>\n" +
                "        <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n" +
                "        <title>Reset Password Email Template</title>\n" +
                "        <meta name=\"description\" content=\"Reset Password Email Template.\">\n" +
                "        <style type=\"text/css\">\n" +
                "            a:hover {\n" +
                "                text-decoration: underline !important;\n" +
                "            }\n" +
                "        </style>\n" +
                "    </head>\n" +
                "\n" +
                "    <body marginheight=\"0\" topmargin=\"0\" marginwidth=\"0\" style=\"margin: 0px; background-color: #f2f3f8;\" leftmargin=\"0\">\n" +
                "        <!--100% body table-->\n" +
                "        <table cellspacing=\"0\" border=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#f2f3f8\"\n" +
                "            style=\"@import url(https://fonts.googleapis.com/css?family=Rubik:300,400,500,700|Open+Sans:300,400,600,700); font-family: 'Open Sans', sans-serif;\">\n" +
                "            <tr>\n" +
                "                <td>\n" +
                "                    <table style=\"background-color: #f2f3f8; max-width:670px;  margin:0 auto;\" width=\"100%\" border=\"0\"\n" +
                "                        align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                        <tr>\n" +
                "                            <td style=\"height:80px;\">&nbsp;</td>\n" +
                "                        </tr>\n" +
                "                        <tr>\n" +
                "                            <td style=\"text-align:center;\">\n" +
                "                                <!-- <a href=\"https://rakeshmandal.com\" title=\"logo\" target=\"_blank\">\n" +
                "                                    <img width=\"60\" src=\"https://i.ibb.co/hL4XZp2/android-chrome-192x192.png\"\n" +
                "                                        title=\"logo\" alt=\"logo\">\n" +
                "                                </a> -->\n" +
                "                            </td>\n" +
                "                        </tr>\n" +
                "                        <tr>\n" +
                "                            <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                        </tr>\n" +
                "                        <tr>\n" +
                "                            <td>\n" +
                "                                <table width=\"95%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                    style=\"max-width:670px;background:#fff; border-radius:3px; text-align:center;-webkit-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);-moz-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);box-shadow:0 6px 18px 0 rgba(0,0,0,.06);\">\n" +
                "                                    <tr>\n" +
                "                                        <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                    </tr>\n" +
                "                                    <tr>\n" +
                "                                        <td style=\"padding:0 35px;\">\n" +
                "                                            <h5\n" +
                "                                                style=\"color:#1e1e2d; font-weight:500; margin:0;font-size:32px;font-family:'Rubik',sans-serif;\">\n" +
                "                                                You have\n" +
                "                                                requested to reset your password</h5>\n" +
                "                                            <span\n" +
                "                                                style=\"display:inline-block; vertical-align:middle; margin:29px 0 26px; border-bottom:1px solid #cecece; width:100px;\"></span>\n" +
                "                                            <p style=\"color:#455056; font-size:15px;line-height:24px; margin:0;\">\n" +
                "                                                We hope this email finds you well. We have received a request to reset\n" +
                "                                                your password for your account with <strong>KLChat</strong>. To ensure\n" +
                "                                                the security of\n" +
                "                                                your account, we are providing you with a <strong>Validation\n" +
                "                                                    Code</strong>.\n" +
                "                                            </p>\n" +
                "                                            <a href=\"javascript:void(0);\"\n" +
                "                                                style=\"background:#20e277;text-decoration:none !important; font-weight:500; margin-top:35px; color:#fff;text-transform:uppercase; font-size:14px;padding:10px 24px;display:inline-block;border-radius:50px;\">\n" +
                "                                                "+ validationCode + "</a>\n" +
                "                                        </td>\n" +
                "                                    </tr>\n" +
                "                                    <tr>\n" +
                "                                        <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                    </tr>\n" +
                "                                </table>\n" +
                "                            </td>\n" +
                "                        <tr>\n" +
                "                            <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                        </tr>\n" +
//                "                        <tr>\n" +
//                "                            <td style=\"text-align:center;\">\n" +
//                "                                <p\n" +
//                "                                    style=\"font-size:14px; color:rgba(69, 80, 86, 0.7411764705882353); line-height:18px; margin:0 0 0;\">\n" +
//                "                                    &copy; <strong>https://github.com/phuquocchamp</strong></p>\n" +
//                "                            </td>\n" +
//                "                        </tr>\n" +
                "                        <tr>\n" +
                "                            <td style=\"height:80px;\">&nbsp;</td>\n" +
                "                        </tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "        <!--/100% body table-->\n" +
                "    </body>\n" +
                "\n" +
                "</html>";

        Session session = Session.getInstance(props, auth);
        MimeMessage mimeMessage= new MimeMessage(session);
        try{
            // Content-type
            mimeMessage.addHeader("Content-type", "text/HTML; charset=UTF-8");
            // Sender
            mimeMessage.setFrom(sendFrom);
            // Receiver
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo, false));


//            mimeMessage.set
            // Email title
            mimeMessage.setSubject(title);
            // Send date
            mimeMessage.setSentDate(new Date());

            // Content
            mimeMessage.setContent(messageSample, "text/HTML; charset=UTF-8");

            Transport.send(mimeMessage);
            System.out.println("Sent mail successfully !");
            return true;



        } catch (Exception e) {
            System.out.println("Facing with error !");
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        sendEmail("hoangtanphuquoc@gmail.com", "Reset Password Request 2", "12344");
    }

}
