package team.work.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("team.work.platform.mapper") // 自动扫描所有 Mapper 接口
public class PlatfromApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PlatfromApplication.class, args);
	}

}
