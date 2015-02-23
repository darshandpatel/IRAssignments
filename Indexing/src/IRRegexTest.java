import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRRegexTest {

	public static void main(String[] args) {
		String text = "The first step of indexing is tokenizing documents from the collection.";
		Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
		Matcher matcher = pattern.matcher(text.toLowerCase());
		int termIndex = 1;
		while (matcher.find()) {
			// USUALLY ZERO
			System.out.println("At while loop level"+matcher.groupCount());
			for (int i = 0; i < matcher.groupCount(); i++) {
				String term = matcher.group(i);
				int startIndex = matcher.start();
				int endIndex = matcher.end();
				System.out.println(termIndex + " " + term + " " + startIndex
						+ " " + endIndex);
				termIndex++;
			}
		}
	}
}