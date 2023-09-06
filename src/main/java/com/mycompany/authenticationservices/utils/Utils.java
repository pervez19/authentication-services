package com.mycompany.authenticationservices.utils;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

public class Utils {
	/**
	 * Check pagination active or not
	 *
	 * @param pageable - Pageable Object
	 * @return Boolean true or false
	 * @author pervez
	 * @since 04 August, 2021
	 */


	public static Boolean isPaginationActive(Pageable pageable){
		if(ObjectUtils.isEmpty(pageable))
			return false;
		if(!ObjectUtils.isEmpty(pageable.getSort()) &&
				!ObjectUtils.isEmpty(pageable.getPageNumber()) &&
				!ObjectUtils.isEmpty(pageable.getPageSize()))
			return true;
		return false;
	}

	/**
	 * Check pagination with sorted order
	 *
	 * @param pageable - Pageable Object
	 * @return Boolean true or false
	 * @author pervez
	 * @since 04 August, 2021
	 */


	public static Boolean isPaginationWithOrderBy(Pageable pageable){
		if(ObjectUtils.isEmpty(pageable))
			return false;
		if(!ObjectUtils.isEmpty(pageable.getSort()) )
			return true;
		return false;
	}

	/**
	 * Check pagination with index size available or not
	 *
	 * @param pageable - Pageable Object
	 * @return Boolean true or false
	 * @author pervez
	 * @since 04 August, 2021
	 */

	public static Boolean isPaginationWithIndexAndSize(Pageable pageable){
		if(ObjectUtils.isEmpty(pageable))
			return false;
		if(!ObjectUtils.isEmpty(pageable.getPageNumber()) &&
				!ObjectUtils.isEmpty(pageable.getPageSize()))
			return true;
		return false;
	}
}

