package com.atlantbh.nutch.index.alternativedataflow.flow;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.codehaus.jackson.map.ObjectMapper;

import com.Ostermiller.util.Base64;
import com.atlantbh.nutch.index.alternativedataflow.FilterUtils;
import com.atlantbh.nutch.index.alternativedataflow.conf.Entry;
import com.atlantbh.nutch.index.alternativedataflow.conf.Field;

public class RestDataFlow implements DataFlow {

	// Constants
	private static final Logger log = Logger.getLogger(RestDataFlow.class);

	// Configuration constants
	public static final QName URL = new QName("URL");

	public static final QName LOGIN = new QName("login");

	public static final QName PASS = new QName("pass");

	// Init data
	private Configuration configuration;

	private HttpClient client;

	private final ObjectMapper mapper = new ObjectMapper();

	private List<Entry> entryList;

	public RestDataFlow() {
	}

	@Override
	public void init(final Configuration configuration, final List<Entry> entryList) {
		this.configuration = configuration;
		this.entryList = entryList;
		this.client = new HttpClient();
	}

	@Override
	public void destroy() {
	}

	@Override
	public void processData(final NutchDocument doc, final Parse parse, final Text url, final CrawlDatum datum,
	        final Inlinks inlinks) throws IndexingException {

		// Get metadata
		Metadata metadata = parse.getData().getParseMeta();

		for (Entry entry : entryList) {

			if ("REST".equals(entry.getDataFlow())) {

				Object[] fieldValues = new Object[entry.getFieldList().size()];
				List<Field> fieldList = entry.getFieldList();
				for (int i = 0; i < fieldList.size(); i++) {
					log.info("Get value for field " + fieldList.get(i).getName());
					if (metadata.isMultiValued(fieldList.get(i).getName())) {
						if ("price".equals(fieldList.get(i).getName())) {
							StringBuilder price = new StringBuilder();
							for (String priceToken : metadata.getValues(fieldList.get(i).getName())) {
								price.append(priceToken);
							}
							fieldValues[i] = price.toString();
						} else {
							fieldValues[i] = metadata.getValues(fieldList.get(i).getName());
						}
					} else {
						fieldValues[i] = FilterUtils.getNullSafe(metadata.get(fieldList.get(i).getName()), "");
						log.info("Single value field " + fieldList.get(i).getName() + ":" + fieldValues[i]);
					}
				}

				CreateProductRequest request = new CreateProductRequest();
				request.setProduct(new Product((String) fieldValues[0], (String) fieldValues[1], (String) fieldValues[2],
				        (String) fieldValues[3], (String[]) fieldValues[4], (String[]) fieldValues[5], url.toString()));

				try {
					PostMethod method = createMethod(entry.getParameterMap().get(URL), entry.getParameterMap().get(LOGIN), entry
					        .getParameterMap().get(PASS), request);
					callMethod(method, SimpleResponse.class);
				} catch (Exception e) {
					log.error("Error" + e.getMessage());
				}

			}
		}
	}

	private <T> T callMethod(final PostMethod method, final Class<T> responseClass) throws Exception {

		int statusCode = client.executeMethod(method);

		if (statusCode != 200) {
			log.info(" statusCode ---> " + statusCode);
		}

		String response = method.getResponseBodyAsString();
		log.info(" response ---> " + response);

		return mapper.readValue(response, responseClass);
	}

	private PostMethod createMethod(final String path, final String login, final String pass, final Object body) throws Exception {
		PostMethod method = new PostMethod(path);
		method.setDoAuthentication(true);
		method.setRequestHeader("Authorization", "Basic " + new String(Base64.encode((login + ":" + pass).getBytes())));
		String request = mapper.writeValueAsString(body);
		log.info(" request url ---> " + path);
		log.info(" request ---> " + request);
		method.setRequestEntity(new StringRequestEntity(request, "application/json", "utf-8"));
		return method;
	}
}
