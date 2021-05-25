import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class HTTPDaemon {

	private final static String CRLF = "\r\n";
	private final static String MIME_TEXT_HTML = "text/html";

	public static void main(String[] args) throws Exception {

		ServerSocket server = new ServerSocket(8080);

		while (true) {
			Socket clientSocket = server.accept();

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					doRequest(clientSocket);
				}
			});

			thread.start();

		}

	}

	public static void doRequest(Socket clientSocket) {
		try {
			InputStream in = clientSocket.getInputStream();
			OutputStream out = clientSocket.getOutputStream();

			String request = getRawRequest(in);
			int fsp = request.indexOf(' ');
			int ssp = request.indexOf(' ', fsp + 1);
			int eol = request.indexOf('\n');
			String method = request.substring(0, fsp);
			String url = request.substring(fsp + 1, ssp);
			System.out.println("Requested: " + url);
			String raw_header = request.substring(eol + 1);
			MimeHeader inmh = new MimeHeader(raw_header);

			if (url.contains("yandex.com")) {
				writeString(out, error(401, "Not Authorized", url));
				in.close();
				out.close();
			} else if (method.equalsIgnoreCase("get")) {
				handleProxy(out, url, inmh);
			}
			clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeString(OutputStream out, String s) throws IOException {
		out.write(s.getBytes());
	}

	private static String error(int code, String msg, String url) {
		String html_page = "<body>" + CRLF + "<h1>" + code + " " + msg + "</h1>" + CRLF;
		if (url != null)
			html_page += "Error when fetching URL: " + url + CRLF;
		html_page += "</body>" + CRLF;
		MimeHeader mh = makeMimeHeader(MIME_TEXT_HTML, html_page.length());
		HTTPResponse hr = new HTTPResponse(code, msg, mh);

		return hr + html_page;
	}

	private static MimeHeader makeMimeHeader(String type, int length) {
		MimeHeader mh = new MimeHeader();
		Date curDate = new Date();
		TimeZone gmtTz = TimeZone.getTimeZone("GMT");
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");
		sdf.setTimeZone(gmtTz);
		mh.put("Date", sdf.format(curDate));
		mh.put("Server", "CSE471/1.0");
		mh.put("Content-Type", type);
		if (length >= 0)
			mh.put("Content-Length", String.valueOf(length));
		return mh;
	}

	public static void handleProxy(OutputStream out, String url, MimeHeader mh) {

		try {
			int start = url.indexOf("://") + 3;
			int path = url.indexOf('/', start);
			String server_url = url.substring(path);
			String site = url.substring(start, path).toLowerCase();

			Socket server = new Socket(site, 80);
			System.out.println("Connected to the " + site);

			InputStream in_server = server.getInputStream();
			OutputStream out_server = server.getOutputStream();

			mh.put("User-Agent", mh.get("User-Agent") + " via CSE471 Proxy");
			mh.put("Connection", "close");

			String rq = "GET " + server_url + " HTTP/1.0\r\n" + mh + "\r\n";

			System.out.println(rq);

			out_server.write(rq.getBytes());

			String raw_response = getRawRequest(in_server);
			System.out.println("Response arrived:\n" + raw_response);
			HTTPResponse server_response = new HTTPResponse(raw_response);
			out.write(server_response.toString().getBytes());

			if (server_response.statusCode == 200) {

				byte[] dat = loadFile(in_server, url, server_response.mh);
				out.write(dat);
			}

			server.close();

		} catch (Exception e) {
		}

	}

	public static byte[] loadFile(InputStream in_server, String url, MimeHeader mh) throws Exception {

		byte[] buffer = new byte[1024 * 1024];

		int size = 0;
		int n;
		while ((n = in_server.read(buffer)) >= 0) {
			size += n;
		}

		byte[] result = new byte[size];
		System.arraycopy(buffer, 0, result, 0, size);
		return result;
	}

	public static String getRawRequest(InputStream in) throws Exception {
		int c;
		int pos = 0;
		byte[] arr = new byte[1024];
		while ((c = in.read()) != -1) {
			switch (c) {
			case '\r':
				break;
			case '\n':
				if (arr[pos - 1] == c) {
					return new String(arr, 0, pos);
				}
			default:
				arr[pos++] = (byte) c;
			}
		}

		return null;

	}

}
