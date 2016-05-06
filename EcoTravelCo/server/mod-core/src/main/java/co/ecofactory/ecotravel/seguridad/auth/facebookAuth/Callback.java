package co.ecofactory.ecotravel.seguridad.auth.facebookAuth;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.PictureSize;
import facebook4j.Reading;
import facebook4j.User;
import facebook4j.auth.AccessToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class Callback extends HttpServlet {

    private static final long serialVersionUID = 6305643034487441839L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Facebook facebook = (Facebook) request.getSession().getAttribute("facebook");
        String oauthCode = request.getParameter("code");
        try {
            facebook.getOAuthAccessToken(oauthCode);
            User user2 = facebook.getMe();
            Reading reading = new Reading().fields("email,name");

            User user = facebook.getUser(user2.getId(), reading);
            System.out.println("................");
            System.out.println("Email " + user.getEmail());
            System.out.println("Name " + user.getName());
            URL largePic = facebook.getPictureURL(user2.getId(), PictureSize.large);
            System.out.println(largePic);
            System.out.println(user);

        } catch (FacebookException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/");
    }
}

