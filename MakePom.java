
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;

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

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element dependencies = doc.createElement("dependencies");
		doc.appendChild(dependencies);
		for (File file : dir.listFiles()) {
			if (accept(file)) {
				System.out.println(file.getName());

				String name = file.getName();
				name = name.substring(0, name.length() - EXT_NAME.length());

				File nf = Paths.get(dir.getAbsolutePath(), "zlocal", "zjar-" + name, "1.0").toFile();
				nf.mkdirs();

				Copy(file, Paths.get(nf.getAbsolutePath(), "zjar-" + name + "-1.0" + EXT_NAME).toString());

				Element dep = doc.createElement("dependency");
				dependencies.appendChild(dep);
				Element groupId = doc.createElement("groupId");
				groupId.setTextContent("zlocal");
				Element artifactId = doc.createElement("artifactId");
				artifactId.setTextContent("zjar-" + name);
				Element version = doc.createElement("version");
				version.setTextContent("1.0");
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
			System.out.println("Done, see the file pom.xml.txt!");
		} catch (Exception ex) {

		}
	}

	static final String EXT_NAME = ".jar";
	static byte[] repositories = ("<repositories>\n<repository>\n<id>in-project</id>\n<name>In Project Repo</name>\n<url>file://${project.basedir}/lib</url>\n</repository>\n</repositories>")
			.getBytes(Charset.forName("UTF-8"));

	static boolean accept(File pathname) {
		if (pathname.getName().toLowerCase().endsWith(EXT_NAME)) {
			return true;
		}
		return false;
	}

	static void Copy(File oldfile, String newPath) {
		try {
			int byteread = 0;
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldfile);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				fs.close();
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("error  ");
			e.printStackTrace();
		}
	}
}
