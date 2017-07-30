
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MakePom {
	public static void main(String[] args) throws ParserConfigurationException {
		File dir = new File(System.getProperty("user.dir"));
		String strGroupId = System.getProperty("groupId");
		String strJarPrefix = System.getProperty("jarPrefix");
		String strJarVersion = System.getProperty("jarVersion");
		if (strGroupId == null || strGroupId.isEmpty())
			strGroupId = "zlocal";
		if (strJarPrefix == null || strJarPrefix.isEmpty())
			strJarPrefix = "zjar";
		if (strJarVersion == null || strJarVersion.isEmpty())
			strJarVersion = "1.0";

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element dependencies = doc.createElement("dependencies");
		doc.appendChild(dependencies);
		for (File file : dir.listFiles()) {
			if (file.getName().toLowerCase().endsWith(EXT_NAME)) {
				System.out.println(file.getName());

				String name = file.getName();
				name = name.substring(0, name.length() - EXT_NAME.length());

				File nf = Paths.get(dir.getAbsolutePath(), strGroupId, strJarPrefix + "-" + name, strJarVersion)
						.toFile();
				nf.mkdirs();

				try {
					Files.copy(file.toPath(),
							Paths.get(nf.getAbsolutePath(), strJarPrefix + "-" + name + "-" + strJarVersion + EXT_NAME),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				Element dep = doc.createElement("dependency");
				dependencies.appendChild(dep);
				Element groupId = doc.createElement("groupId");
				groupId.setTextContent(strGroupId);
				Element artifactId = doc.createElement("artifactId");
				artifactId.setTextContent(strJarPrefix + "-" + name);
				Element version = doc.createElement("version");
				version.setTextContent(strJarVersion);
				dep.appendChild(groupId);
				dep.appendChild(artifactId);
				dep.appendChild(version);
			}
		}

		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			FileOutputStream stream = new FileOutputStream(Paths.get(dir.getAbsolutePath(), "pom.xml.txt").toString());
			PrintWriter pw = new PrintWriter(stream);
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
			stream.write(repositories);
			stream.close();
			System.out.println("Done, see the file pom.xml.txt.");
		} catch (Exception ex) {

		}
	}

	static final String EXT_NAME = ".jar";
	static byte[] repositories = ("<repositories>\n<repository>\n<id>in-project</id>\n<name>In Project Repo</name>\n<url>file://${project.basedir}/lib</url>\n</repository>\n</repositories>")
			.getBytes(Charset.forName("UTF-8"));
}
