package com.mycompany.authenticationservices.searchCriteria;

import lombok.Data;
import lombok.experimental.Accessors;

@Data @Accessors(chain=true)
public class Pagination {

	private   Boolean isPageResult=true;
	private   Integer pageNo=0;
	private   Integer pageSize=10;
	private   Integer tzoff=0;

}
