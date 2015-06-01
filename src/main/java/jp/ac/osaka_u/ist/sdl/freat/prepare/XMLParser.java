package jp.ac.osaka_u.ist.sdl.freat.prepare;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser implements ConfigConstant {

	public Config parse(final String xmlPath) throws Exception {
		final Config config = new Config();

		final DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		final File file = new File(xmlPath);
		if (!file.exists()) {
			throw new IllegalArgumentException(xmlPath + " does not exist");
		}

		final Node root = builder.parse(file);

		processRoot(root, config);

		return config;
	}

	private String retrieveNodeValue(final Node core) {
		final String nodeName = core.getNodeName();

		if (nodeName != null && !nodeName.isEmpty()
				&& !nodeName.equals("#text")) {
			final Node firstChild = core.getFirstChild();

			if (firstChild.getNodeValue() != null) {
				final String firstChildValue = firstChild.getNodeValue().trim();

				if (firstChild.getNodeName().equals("#text")
						&& !firstChildValue.isEmpty()) {
					return firstChildValue;
				}
			}
		}

		// cannot find
		return null;
	}

	private void processRoot(final Node node, final Config config)
			throws Exception {
		final NodeList listChildren = node.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			switch (child.getNodeName()) {
			case NODE_NAME_CONFIG:
				processConfig(child, config);
				break;
			}
		}
	}

	private void processConfig(final Node node, final Config config)
			throws Exception {
		final NodeList listChildren = node.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			switch (child.getNodeName()) {
			case NODE_NAME_GENERAL:
				processGeneral(child, config);
				break;
			case NODE_NAME_TARGETS:
				processTargets(child, config);
				break;
			case NODE_NAME_SERVLET:
				processServlet(child, config);
			}
		}
	}

	private void processGeneral(final Node node, final Config config)
			throws Exception {
		final NodeList listChildren = node.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			switch (child.getNodeName()) {
			case NODE_NAME_THREAD:
				config.setThreads(Integer.parseInt(retrieveNodeValue(child)));
				break;
			case NODE_NAME_BATCH:
				config.setBatch(Integer.parseInt(retrieveNodeValue(child)));
				break;
			case NODE_NAME_DATABASE:
				config.setDbPath(retrieveNodeValue(child));
				break;
			}
		}
	}

	private void processTargets(final Node node, final Config config)
			throws Exception {
		final NodeList listChildren = node.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			switch (child.getNodeName()) {
			case NODE_NAME_TARGET:
				processTarget(child, config);
				break;
			}
		}
	}

	private void processTarget(final Node node, final Config config)
			throws Exception {
		final Repository repository = new Repository();

		final NodeList listChildren = node.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			switch (child.getNodeName()) {
			case NODE_NAME_TARGET_NAME:
				repository.setName(retrieveNodeValue(child));
				break;
			case NODE_NAME_REPOSITORY:
				repository.setUrl(retrieveNodeValue(child));
				break;
			case NODE_NAME_RELATIVE:
				repository.setRelative(retrieveNodeValue(child));
				break;
			case NODE_NAME_START:
				repository.setStart(Integer.parseInt(retrieveNodeValue(child)));
				break;
			case NODE_NAME_END:
				repository.setEnd(Integer.parseInt(retrieveNodeValue(child)));
				break;
			}
		}

		config.addRepository(repository);
	}

	private void processServlet(final Node node, final Config config)
			throws Exception {
		final NodeList listChildren = node.getChildNodes();
		for (int i = 0; i < listChildren.getLength(); i++) {
			final Node child = listChildren.item(i);

			switch (child.getNodeName()) {
			case NODE_NAME_PORT:
				config.setPort(Integer.parseInt(retrieveNodeValue(child)));
				break;
			case NODE_NAME_URLPATTERN:
				config.setUrlPattern(retrieveNodeValue(child));
				break;
			}
		}
	}

}
