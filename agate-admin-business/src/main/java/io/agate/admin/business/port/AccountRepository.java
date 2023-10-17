package io.agate.admin.business.port;

import io.agate.admin.business.model.UserDefinition;

public interface AccountRepository {

	UserDefinition find(String username);

}
