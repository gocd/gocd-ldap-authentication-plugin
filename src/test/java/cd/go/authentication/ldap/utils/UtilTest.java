package cd.go.authentication.ldap.utils;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

public class UtilTest {

    @Test
    public void listFromCommaSeparatedString_shouldParseCommaSeparatedStringWithoutSpace() throws Exception {
        String csvStringWithoutSpace = "uid,sAmAccountName,mail,displayName,name";
        List<String> resultOfWithoutSpace = Util.listFromCommaSeparatedString(csvStringWithoutSpace);
        assertThat(resultOfWithoutSpace, hasSize(5));
        assertThat(resultOfWithoutSpace, contains("uid", "sAmAccountName", "mail", "displayName", "name"));
    }

    @Test
    public void listFromCommaSeparatedString_shouldParseCommaSeparatedStringWithSpace() throws Exception {
        String csvStringWithoutSpace = "uid   ,    sAmAccountName        ,          mail ,         displayName       ,        name";
        List<String> resultOfWithoutSpace = Util.listFromCommaSeparatedString(csvStringWithoutSpace);
        assertThat(resultOfWithoutSpace, hasSize(5));
        assertThat(resultOfWithoutSpace, contains("uid", "sAmAccountName", "mail", "displayName", "name"));
    }

    @Test
    public void listFromCommaSeparatedString_shouldParseCommaSeparatedStringWithNewLine() throws Exception {
        String csvStringWithoutSpace = "uid,\n" +
                "sAmAccountName,\n" +
                "mail,\n" +
                "displayName,\n" +
                "name";
        List<String> resultOfWithoutSpace = Util.listFromCommaSeparatedString(csvStringWithoutSpace);
        assertThat(resultOfWithoutSpace, hasSize(5));
        assertThat(resultOfWithoutSpace, contains("uid", "sAmAccountName", "mail", "displayName", "name"));
    }
}