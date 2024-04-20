package com.example.homestay.email;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.homestay.booking.Booking;
import com.example.homestay.homestayroom.Room;
import com.example.homestay.homestaytype.Type;
import com.example.homestay.user.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    public void sendEmail(Email email, User user,Room room, Type type, Booking booking) {
        //Basic thôi
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setFrom("khoab1910240@gmail.com");
        // message.setTo(toEmail);
        // message.setText(body);
        // message.setSubject(subject);
        // mailSender.send(message);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"); 
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("Dormitory@gmail.com");
            helper.setTo(email.getToEmail());
            helper.setSubject(email.getSubject());

            // Tạo nội dung HTML với các thẻ và CSS tùy chỉnh
            String htmlContent = "<!DOCTYPE html>\r\n" + //
                    "<html>\r\n" + //
                    "  <head>\r\n" + //
                    "    <style>\r\n" + //
                    "      /* CSS cho phần nội dung email */\r\n" + //
                    "      body {\r\n" + //
                    "        font-family: Arial, sans-serif;\r\n" + //
                    "      }\r\n" + //
                    "      .email-container {\r\n" + //
                    "        max-width: 800px;\r\n" + //
                    "        margin: 0 auto;\r\n" + //
                    "        padding: 20px;\r\n" + //
                    "        background-color: #f0f0f0;\r\n" + //
                    "      }\r\n" + //
                    "      .header {\r\n" + //
                    "        background-color: rgba(6, 12, 34, 0.98);\r\n" + //
                    "        color: #ffffff;\r\n" + //
                    "        text-align: center;\r\n" + //
                    "        padding: 10px;\r\n" + //
                    "      }\r\n" + //
                    "      table {\r\n" + //
                    "        width: 100%;\r\n" + //
                    "        margin-bottom: 20px;\r\n" + //
                    "        border: 1px solid #ddd;\r\n" + //
                    "        border-collapse: collapse;\r\n" + //
                    "        border: none;\r\n" + //
                    "      }\r\n" + //
                    "      th,\r\n" + //
                    "      td {\r\n" + //
                    "        padding: 10px;\r\n" + //
                    "      }\r\n" + //
                    "      .footer {\r\n" + //
                    "        background-color: rgba(6, 12, 34, 0.98);\r\n" + //
                    "        color: #ffffff;\r\n" + //
                    "        text-align: center;\r\n" + //
                    "        padding: 10px;\r\n" + //
                    "      }\r\n" + //
                    "      .title {\r\n" + //
                    "        font-weight: bold;\r\n" + //
                    "      }\r\n" + //
                    "      * {\r\n" + //
                    "        margin: 0;\r\n" + //
                    "        padding: 0;\r\n" + //
                    "      }\r\n" + //
                    "    </style>\r\n" + //
                    "  </head>\r\n" + //
                    "  <body>\r\n" + //
                    "    <div class=\"email-container\">\r\n" + //
                    "      <div class=\"header\">\r\n" + //
                    "        <h3 style=\"font-weight: bold\">\r\n" + //
                    "          CHÚC MỪNG BẠN ĐẶT HOMESTAY THÀNH CÔNG\r\n" + //
                    "        </h3>\r\n" + //
                    "      </div>\r\n" + //
                    "      <table style=\"background-color: white\">\r\n" + //
                    "        <tr>\r\n" + //
                    "          <td class=\"title\">Tài khoản:</td>\r\n" + //
                    "          <td>"+user.getUsername()+"</td>\r\n" + //
                    "        </tr>\r\n" + //
                    "        <tr>\r\n" + //
                    "          <td class=\"title\">Tên:</td>\r\n" + //
                    "          <td>"+user.getFullname()+"</td>\r\n" + //
                    "        </tr>\r\n" + //
                    "        <tr>\r\n" + //
                    "          <td class=\"title\">Số điện thoại:</td>\r\n" + //
                    "          <td>"+user.getPhoneNumber()+"</td>\r\n" + //
                    "        </tr>\r\n" + //
                    "        <tr>\r\n" + //
                    "          <td class=\"title\">Email:</td>\r\n" + //
                    "          <td>"+user.getEmail()+"</td>\r\n" + //
                    "        </tr>\r\n" + //
                    "        <tr>\r\n" + //
                    "          <td class=\"title\">Địa chỉ:</td>\r\n" + //
                    "          <td>"+user.getAddress()+"</td>\r\n" + //
                    "        </tr>\r\n" + //
                    
                    "      </table>\r\n" + //
                    
                    "      <h4 style=\"text-align: center\">Thông tin homestay</h4>\r\n" + //
                    "      <table style=\"background-color: white\">\r\n" + //
                    "        <tr>\r\n" + //
                    "          <td class=\"title\">Loại homestay:</td>\r\n" + //
                    "          <td>"+type.getTypeName()+"</td>\r\n" + //
                    "        </tr>\r\n" + //
                    "        <tr>\r\n" + //
                    "          <td class=\"title\">Homestay:</td>\r\n" + //
                    "          <td>"+room.getRoomName()+"</td>\r\n" + //
                    "        </tr>\r\n" + //
                   
                   
                    "        <tr>\r\n" + //
                    "          <td class=\"title\">Ngày nhận phòng :</td>\r\n" + //
                    "          <td>"+formatter.format(booking.getStartDateTime())+"</td>\r\n" + //
                    "        </tr>\r\n" + //
                    "        <tr>\r\n" + //
                    "          <td class=\"title\">Ngày trả phòng:</td>\r\n" + //
                    "          <td>"+formatter.format(booking.getEndDateTime())+"</td>\r\n" + //
                    "        </tr>\r\n" + //
                    "      </table>\r\n" + //
                    "      <div>\r\n" + //
                    "        <div>\r\n" + //
                   
                    
                    "          <p\r\n" + //
                    "            style=\"\r\n" + //
                    "              font-size: 13px;\r\n" + //
                    "              color: rgb(17, 20, 23);\r\n" + //
                    "              margin: 13px 0px;\r\n" + //
                    "              padding: 0px;\r\n" + //
                    "              font-family: Arial, 'Arial Unicode MS', Helvetica, sans-serif;\r\n" + //
                    "              text-align: justify;\r\n" + //
                    "            <span style=\"font-size: 10pt\"\r\n" + //
                    "              ><strong\r\n" + //
                    "                ><em\r\n" + //
                    "                  >&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<u>Lưu ý:</u></em\r\n" + //
                    "                ></strong\r\n" + //
                    "              >&nbsp;Nếu không tham gia đúng thời hạn đăng ký, bạn sẽ bị <em\r\n" + //
                    "                ><b>KHÓA TÀI KHOẢN</b></em\r\n" + //
                    "              >&nbsp;, và không thể đăng ký ở các homestay khác.<strong\r\n" + //
                    "                >&nbsp;</strong\r\n" + //
                    "              >&nbsp;\r\n" + //
                    "              &nbsp;<em\r\n" + //
                    "                ></em\r\n" + //
                    "              >&nbsp;</span\r\n" + //
                    "            ><br />\r\n" + //fn
                    "          </p>\r\n" + //
                    "          <ol\r\n" + //
                    "            start=\"4\"\r\n" + //
                    "            style=\"\r\n" + //
                    "              font-size: 13px;\r\n" + //
                    "              color: rgb(17, 20, 23);\r\n" + //
                    "              margin: 1em 0px 1em 11px;\r\n" + //
                    "              padding: 0px;\r\n" + //
                    "              list-style-position: inside;\r\n" + //
                    "              font-family: Arial, 'Arial Unicode MS', Helvetica, sans-serif;\r\n" + //
                    "              text-align: justify;\r\n" + //
                    "            \"\r\n" + //
                    "          >\r\n" + //
                    
                    "          </ol>\r\n" + //
                    "\r\n" + //
                   
                    "          <div style=\"text-align: justify\">\r\n" + //
                    "            <font\r\n" + //
                    "              color=\"#111417\"\r\n" + //
                    "              face=\"Arial, Arial Unicode MS, Helvetica, sans-serif\"\r\n" + //
                    "              ><span style=\"font-size: 13.3333px\"\r\n" + //
                    "                ></span\r\n" + //
                    "              ></font\r\n" + //
                    "            >\r\n" + //
                    "          </div>\r\n" + //
                    "          <div style=\"text-align: justify\">\r\n" + //
                    "            <font\r\n" + //
                    "              color=\"#111417\"\r\n" + //
                    "              face=\"Arial, Arial Unicode MS, Helvetica, sans-serif\"\r\n" + //
                    "              ><span style=\"font-size: 13.3333px\">Trân trọng./.</span></font\r\n" + //
                    "            >\r\n" + //
                    "          </div>\r\n" + //
                    "        </div>\r\n" + //
                    "      </div>\r\n" + //
                    "      <div class=\"footer\">\r\n" + //
                    "        <p>Homestay TheEvent</p>\r\n" + //
                    "        <p>Cung cấp dịch vụ trải nghiệm tuyệt vời</p>\r\n" + //
                    "        <p>\r\n" + //
                    "          Điện thoại Văn phòng: 0292.3872275 - Điện thoại di động: 0975 185 994\r\n" + //
                    "          (Zalo)\r\n" + //
                    "        </p>\r\n" + //
                    "      </div>\r\n" + //
                    "    </div>\r\n" + //
                    "  </body>\r\n" + //
                    "</html>\r\n" + //
                    "";

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
