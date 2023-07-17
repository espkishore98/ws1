package com.app.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.app.entity.Customer;
import com.app.listener.StepSkipListener;
import com.app.repository.CustomerRepository;

@Configuration
@EnableBatchProcessing
//@AllArgsConstructor
public class SpringBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerItemWriter customerItemWriter;
	
	private List<Resource> processedResources = new ArrayList<>();

//    @Value("${file.path}")
//	private String filePath;

//    @Bean
//   //@StepScope
//    public FlatFileItemReader<Customer> reader() {   //@Value("#{jobParameters['filePath']}") String filePath
//        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//        
//        itemReader.setResource(new FileSystemResource(filePath));
//        itemReader.setName("csvReader");
//        itemReader.setLinesToSkip(1);
//        itemReader.setLineMapper(lineMapper());
//        return itemReader;
//    }

	@Value("#{'${file.paths}'.split(',')}")
	private List<String> filePaths;

	@Bean
	public MultiResourceItemReader<Customer> reader() {
		MultiResourceItemReader<Customer> multiReader = new MultiResourceItemReader<>();
		multiReader.setName("csvReader");
		multiReader.setResources(getFileResources());
		multiReader.setDelegate(singleFileReader());
		multiReader.setStrict(false);
		multiReader.setSaveState(false);
		return multiReader;
	}

//	private Resource[] getFileResources() {
//	    Resource[] resources = new Resource[filePaths.size()];
//	    for (int i = 0; i < filePaths.size(); i++) {
//	        Resource resource = new FileSystemResource(filePaths.get(i));
//	        System.out.println("file reading name: " + filePaths.get(i));
//	        resources[i] = resource;
//	        processedResources.add(resource);
//	    }
//	    System.out.println("all files reading data");
//	    return resources;
//	}
	
	private Resource[] getFileResources() {
	    List<Resource> allResources = new ArrayList<>();
	    for (String filePath : filePaths) {
	        Resource[] resources = getResourcesFromFile(filePath);
	        allResources.addAll(Arrays.asList(resources));
	    }
	    return allResources.toArray(new Resource[0]);
	}

	private Resource[] getResourcesFromFile(String filePath) {
	    try {
	        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	        return resolver.getResources("file:" + filePath);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return new Resource[0];
	}

	private FlatFileItemReader<Customer> singleFileReader() {
		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Customer> lineMapper() {

		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;

	}

	@Bean
	public CustomerProcessor processor() {
		return new CustomerProcessor();
	}

//	@Bean
//	public RepositoryItemWriter<Customer> writer() {
//		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
//		writer.setRepository(customerRepository);// use it save the reader object to Db
//		writer.setMethodName("save");
//		return writer;
//	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("csv-step").<Customer, Customer>chunk(100)
				.reader(reader())
				.processor(processor())
				//.writer(writer())
				.writer(customerItemWriter)
				.faultTolerant()
				.skipLimit(1000)
//              .skip(Exception.class)
//              .noSkip(IllegalArgumentException.class)
				.listener(skipListener())
				.skipPolicy(skipPolicy())
				//.taskExecutor(taskExecutor())
				.build();
	}

	@Bean
	public Job runJob() {
		return jobBuilderFactory.get("importCustomers")
				.flow(step1())
				//.next(step2())  
				.end()
				.build();

	}

	// by default batch synchronous in order make asynchronous use taskExecutor
//	@Bean
//	public TaskExecutor taskExecutor() {
//		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
//		asyncTaskExecutor.setConcurrencyLimit(10);
//		return asyncTaskExecutor;
//	}

	@Bean
	public SkipPolicy skipPolicy() {
		return new ExceptionSkipPolicy();
	}

	@Bean
	public SkipListener<?, ?> skipListener() {

		System.out.println("calling the skiplistener---------");
		return new StepSkipListener();
	}
}