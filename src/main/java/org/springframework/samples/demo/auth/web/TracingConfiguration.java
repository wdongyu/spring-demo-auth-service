package org.springframework.samples.demo.auth.web;

import org.apache.log4j.Logger;

import org.springframework.cloud.sleuth.SpanAdjuster;
import org.springframework.cloud.sleuth.Span;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * This adds tracing configuration to any web mvc controllers or rest template clients.
 *
 * <p>This is a {@link Initializer#getRootConfigClasses() root config class}, so the
 * {@linkplain DelegatingTracingFilter} added in {@link Initializer#getServletFilters()} can wire
 * up properly.
 */
@Configuration
public class TracingConfiguration {

  /** Configuration for how to send spans to Zipkin */
  /*@Bean Sender sender() {
    return OkHttpSender.create("http://localhost:9411/api/v2/spans");
  }*/

  /** Configuration for how to buffer spans into messages for Zipkin */
  /*@Bean AsyncReporter<Span> spanReporter() {
    return AsyncReporter.builder(sender()).build();
  }*/

  /*@Bean 
  public SpanAdjuster spanAdjuster() {
    //return span -> span.toBuilder().name("wdongyu-db-service").build();
    return new SpanAdjuster(){
    
      @Override
      public Span adjust(Span span) {
        Logger logger = Logger.getLogger("auth-service ~~~~~~~~~");
        Properties properties = System.getProperties();
        String gitFilePath = properties.getProperty("user.dir") + "/target/classes/git.properties";
        File gitFile = new File(gitFilePath);
        try {
          if ( gitFile.isFile() && gitFile.exists()) {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(gitFile));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String tmp = null;
            while ((tmp = bufferedReader.readLine()) != null) {
              if (tmp.startsWith("git.commit.id")) {
                int index = tmp.indexOf('=');
                tmp = tmp.substring(index+1, index+8);
                break;
              }
            }
            bufferedReader.close();
            inputStreamReader.close();
            return span.toBuilder().name(tmp).build();
          }
        } catch (Exception e) {
          //TODO: handle exception
          logger.info(e);
        }
        return span.toBuilder().name("wdongyu-auth-service").build();
      }
    };
  }*/
}