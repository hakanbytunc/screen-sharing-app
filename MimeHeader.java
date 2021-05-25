import java.util.*;

public class MimeHeader extends HashMap<String, String> {
	void parse(String data) {
		StringTokenizer st = new StringTokenizer(data, "\r\n");

		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			int colon = s.indexOf(':');
			String key = s.substring(0, colon); // Connection: close
			String value = s.substring(colon + 1);
			put(key, value);
		}
	}

	public MimeHeader() {
	}

	public MimeHeader(String s) {
		parse(s);
	}

	@Override
	public synchronized String toString() {
		String ret = "";
		Iterator<String> e = keySet().iterator();
		while (e.hasNext()) {
			String key = e.next();
			String val = get(key);
			ret += key + ": " + val + "\r\n";
		}
		return ret;
	}

}
