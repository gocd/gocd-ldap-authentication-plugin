/*
 * Copyright 2022 Thoughtworks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.authentication.ldap.validators;

import cd.go.plugin.base.validation.ValidationResult;
import cd.go.plugin.base.validation.Validator;

import java.util.Map;

import static cd.go.authentication.ldap.utils.Util.isBlank;
import static cd.go.authentication.ldap.utils.Util.isNotBlank;

public class ManagerCredentialValidator implements Validator {
    @Override
    public ValidationResult validate(Map<String, String> authConfigAsMap) {
        ValidationResult validationResult = new ValidationResult();
        if (isNotBlank(authConfigAsMap.get("ManagerDN")) && isBlank(authConfigAsMap.get("Password"))) {
            validationResult.add("Password", "Password cannot be blank when ManagerDN is provided.");
        }
        return validationResult;
    }
}
