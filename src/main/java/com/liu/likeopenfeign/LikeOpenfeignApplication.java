package com.liu.likeopenfeign;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class LikeOpenfeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(LikeOpenfeignApplication.class, args);
//        RequestObject request = new RequestObject("ptg", null, "ptg", null);
//        WebClient webClient = WebClient.create("127.0.0.1", 8081);
//        HttpHeaders httpHeaders = new DefaultHttpHeaders();
//        httpHeaders.set(HttpHeaderNames.AUTHORIZATION, "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJidXNpbmVzcyI6IlJTTCIsInJvbGVDb2RlIjoiUkJfR0VORVJBTF9NQU5BR0VSIiwiaXNBZG1pbiI6ZmFsc2UsInVzZXJOYW1lIjoiU1lTIDAwMDEiLCJleHAiOjE2MDE1NDc4NzYsImNlbiI6IjMxIiwidXNlckNvZGUiOiJTWVMwMDAxIn0.umYZGGW7eLeZphdNCaYSoDbvMAK3ofrUZO-80wU_xiw");
//        ResponseEntity<ResponseObject> responseEntity = webClient.uri("/outbound/api/v1/run-command")
//                .header(httpHeaders)
//                .post()
//                .bodyValue(request)
//                .retrieve()
//                .toEntity(ResponseObject.class);
//        System.out.println(responseEntity.response());
    }

}
