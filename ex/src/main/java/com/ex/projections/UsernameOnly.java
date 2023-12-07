package com.ex.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    //인터페이스 기반 Closed Projections
//    String getUsername();

    //인터페이스 기반 Open Projections
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();

}
