package run.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages={"run"})
@Component
public class MongoCommonConfiguration extends AbstractMongoConfiguration {
    @Value("${mongo.host}")
    private String host;
    
    private List<Converter<?,?>> converters = new ArrayList<Converter<?,?>>();

	@Override
	protected String getDatabaseName() {
		return "run";
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient(host);
	}

	@Override
	public CustomConversions customConversions() {
		return new CustomConversions(converters);
	}

	@Override
	protected String getMappingBasePackage() {
		return "run.repo";
	}

}
