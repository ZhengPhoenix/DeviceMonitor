package com.phoenix.devicemonitor.service;

import android.os.AsyncTask;
import android.util.Log;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * Created by hui1.zheng on 9/9/2015.
 */
public class MailSender extends AsyncTask{

    private static final String TAG = "MailSender";

    private static final String SMTP_SERVER = "smtp.qq.com";
    private static final String USER_NAME = "342972949@qq.com";
    private static final String USER_PSW = "heaventear";

    private String mFrom;
    private String mToList;
    private String mCcList;
    private String mSubject;
    private String mTxtBody;
    private String mHtmlBody;

    private boolean authenticationRequired = false;

    public MailSender(String from, String to, String subject, String txtBody, String htmlBody) {
        this.mFrom = from;
        this.mToList = to;
        this.mCcList = null;
        this.mSubject = subject;
        this.mTxtBody = txtBody;
        this.mHtmlBody = htmlBody;

        this.authenticationRequired = true;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            sendAuthenticated();
        } catch (Exception e ){
            Log.d(TAG, "send email test failed, e:" + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    public void sendAuthenticated() throws AddressException, MessagingException {
        authenticationRequired = true;
        send();
    }

    public void send() throws AddressException, MessagingException {

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.port", "465");
        props.put("mail.user", this.mFrom);
        props.setProperty("mail.smtp.ssl.enable", "true");


        Session session;
        if(this.authenticationRequired) {
            Authenticator auth = new SMTPAuthenticator();
            props.put("mail.smtp.auth", "true");
            session = Session.getDefaultInstance(props, auth);
        } else {
            session = Session.getDefaultInstance(props, null);
        }

        session.setDebug(true);

        Message msg = new MimeMessage(session);

        try {
            msg.setFrom(new InternetAddress(mFrom, mFrom));
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(mFrom, mFrom)});
        } catch (Exception e) {
            msg.setFrom(new InternetAddress(mFrom));
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(mFrom)});
        }

        msg.setSentDate(Calendar.getInstance().getTime());

        StringTokenizer tokenizer = new StringTokenizer(mToList, ",");
        int numberOfRecipients = tokenizer.countTokens();

        InternetAddress[] addressTo = new InternetAddress[numberOfRecipients];

        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            addressTo[i++] = new InternetAddress(tokenizer.nextToken());
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        //ignore CC,BCC,ReplyTo

        msg.setHeader("X-Mailer", "Mail Sender");
        msg.setHeader("Precedence", "bulk");
        //set subject and content type
        msg.setSubject(mSubject);

        Multipart mp = new MimeMultipart("related");

        //set body message
        MimeBodyPart bodyMsg = new MimeBodyPart();
        bodyMsg.setText(mTxtBody, "iso-8859-1");

        bodyMsg.setContent(mHtmlBody, "text/html");
        mp.addBodyPart(bodyMsg);

        msg.setContent(mp);


        try {
            Transport.send(msg);
        } catch (Exception e) {
            Log.d(TAG, "send email failed , e :" + e.getMessage());
        }
    }

    private static class SMTPAuthenticator extends Authenticator {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

            String username = USER_NAME;
            String password = USER_PSW;

            return new PasswordAuthentication(username, password);
        }
    }

    public String getmFrom() {
        return mFrom;
    }

    public void setmFrom(String mFrom) {
        this.mFrom = mFrom;
    }

    public String getmToList() {
        return mToList;
    }

    public void setmToList(String mToList) {
        this.mToList = mToList;
    }

    public String getmCcList() {
        return mCcList;
    }

    public void setmCcList(String mCcList) {
        this.mCcList = mCcList;
    }

    public String getmSubject() {
        return mSubject;
    }

    public void setmSubject(String mSubject) {
        this.mSubject = mSubject;
    }

    public String getmTxtBody() {
        return mTxtBody;
    }

    public void setmTxtBody(String mTxtBody) {
        this.mTxtBody = mTxtBody;
    }

    public String getmHtmlBody() {
        return mHtmlBody;
    }

    public void setmHtmlBody(String mHtmlBody) {
        this.mHtmlBody = mHtmlBody;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    public void setAuthenticationRequired(boolean authenticationRequired) {
        this.authenticationRequired = authenticationRequired;
    }
}
