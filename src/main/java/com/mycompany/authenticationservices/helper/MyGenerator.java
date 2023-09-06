package com.mycompany.authenticationservices.helper;

import java.io.Serializable;
import java.time.Instant;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class MyGenerator implements IdentifierGenerator {
	public static final String generatorName = "myGenerator";

	@Override
	public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object object) throws HibernateException {
		long timestamp = Instant.now().toEpochMilli();
		long random = new Random().nextInt(1000000);
		return String.format("%d-%06d", timestamp, random).replace("-","");
	}
}
