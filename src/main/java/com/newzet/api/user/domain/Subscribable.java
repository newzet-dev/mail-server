package com.newzet.api.user.domain;

import com.newzet.api.newsletter.domain.Newsletter;

public interface Subscribable {
	public void subscribe(Newsletter newsletter);

	public boolean isSubscribed(Newsletter newsletter);

	public int countSubscription();
}
