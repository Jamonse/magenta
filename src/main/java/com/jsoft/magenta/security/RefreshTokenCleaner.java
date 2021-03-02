package com.jsoft.magenta.security;

import com.jsoft.magenta.security.dao.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleaner
{
    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    public void performOnConstruct()
    {
        performDailyTokenCleanJob();
    }

    @Scheduled(cron = "${application.schedule.token-job}")
    public void performScheduled()
    {
        performDailyTokenCleanJob();
    }

    private void performDailyTokenCleanJob()
    {
        this.refreshTokenRepository.removeExpiredTokens();
    }
}
