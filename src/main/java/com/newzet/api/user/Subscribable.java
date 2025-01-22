package com.newzet.api.user;

import com.newzet.api.newsletter.Newsletter;

public interface Subscribable {
	public void subscribe(Newsletter newsletter);

	public boolean isSubscribed(Newsletter newsletter);

	public int countSubscription();
}
