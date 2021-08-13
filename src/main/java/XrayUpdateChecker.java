import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import org.apache.commons.codec.binary.Base64;
import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Scanner;
import static io.restassured.RestAssured.given;

/**
 * Tool to extract the "real" last updated Jira scenarios from the Xray import response.
 * It gets the id scenarios from the Xray import response and compares the jira scenario update date with the time
 * of the Xray import execution to evaluate which scenarios actually were modified or added in the last import request.
 * Result is saved in ../updatedTests.tmp
 */
public class XrayUpdateChecker {

    /**
     * Main method
     * @param args
     * args[0] = xray_response File with xray import response (jira Ids to check)
     */
    public static void main(String[] args) {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();
        String jiraToken = new String(Base64.encodeBase64(environmentVariables.getProperty("jiraToken").getBytes()));

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
            Date uploadTimestamp = df.parse(environmentVariables.getProperty("uploadTimestamp"));
            long updateLapse = Long.parseLong(environmentVariables.getProperty("updateLapse"));
            String result ="";

            Scanner scanner = new Scanner(new File(args[0]));
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();
                String testId = line.substring(1, line.length()-1);

                RequestSpecification requestSpecification = given();
                requestSpecification.contentType(ContentType.fromContentType("application/json"));
                requestSpecification.header(new Header("Authorization", "Basic " + jiraToken));
                Response response = requestSpecification.get("https://staging.tools.adidas-group.com/jira/rest/api/2/issue/" + testId);

                JsonPath jsonPathEvaluator = response.jsonPath();

                Date updateDate = df.parse(jsonPathEvaluator.get("fields.updated").toString());

                System.out.println("jira update date: " + updateDate.toString());
                System.out.println("xray upload date: " + uploadTimestamp.toString());

                Duration d = Duration.between(updateDate.toInstant(), uploadTimestamp.toInstant());
                System.out.println("d = " + d.toMillis());

                if (d.toMillis() <= updateLapse) {
                    if (!result.isEmpty()) {
                        result = result.concat(", ");
                    }
                    result = result.concat(testId);
                }
            }

            if (result.isEmpty()) {
                result = "No tests where updated or uploaded in the last request";
            }

            PrintWriter out = new PrintWriter("../updatedTests.tmp");
            out.println(result);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
