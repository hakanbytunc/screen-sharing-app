
public class HTTPResponse {

	int statusCode;
	String reasonPhrase;
	MimeHeader mh;

	void parse(String rq) {
		int fsp = rq.indexOf(' ');
		int ssp = rq.indexOf(' ', fsp + 1);
		int eol = rq.indexOf('\n');
		statusCode = Integer.parseInt(rq.substring(fsp + 1, ssp));
		reasonPhrase = rq.substring(ssp + 1, eol);
		String raw_mime_header = rq.substring(eol + 1);
		mh = new MimeHeader(raw_mime_header);
	}

	public HTTPResponse(String request) {
		parse(request);
	}

	public HTTPResponse(int code, String reason, MimeHeader m) {
		statusCode = code;
		reasonPhrase = reason;
		mh = m;
	}

	@Override
	public String toString() {
		return "HTTP/1.0 " + statusCode + " " + reasonPhrase + "\r\n" + mh + "\r\n";
	}

}
