/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jabis.refund.core.security.token.jwt;

import org.springframework.util.Assert;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ClaimAccessor {

	Map<String, Object> getClaims();

	default <T> T getClaim(String claim) {
		return !hasClaim(claim) ? null : (T) getClaims().get(claim);
	}

	default boolean hasClaim(String claim) {

		Assert.notNull(claim, "claim cannot be null");
		return getClaims().containsKey(claim);
	}

	default String getClaimAsString(String claim) {
		return !hasClaim(claim) ? null
				: getClaims().get(claim).toString();
	}

	default Instant getClaimAsInstant(String claim) {
		if (!hasClaim(claim)) {
			return null;
		}
		return (Instant) getClaims().get(claim);
	}

	default URL getClaimAsURL(String claim) {
		if (!hasClaim(claim)) {
			return null;
		}
		return (URL) getClaims().get(claim);
	}

	default List<String> getClaimAsStringList(String claim) {
		if (!hasClaim(claim)) {
			return null;
		}
		return (List<String>) getClaims().get(claim);
	}
}
