package mpaf.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

public class DefaultCacheServlet extends DefaultServlet {
	private static final long serialVersionUID = 6258969236626914580L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.HOUR, 1);
		resp.setHeader("Expires", htmlExpiresDateFormat().format(cal.getTime()));
		super.doGet(req, resp);
	}

	public DateFormat htmlExpiresDateFormat() {
		DateFormat httpDateFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return httpDateFormat;
	}

}
