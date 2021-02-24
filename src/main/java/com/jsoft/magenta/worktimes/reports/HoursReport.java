package com.jsoft.magenta.worktimes.reports;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public abstract class HoursReport
{
    protected String userName;
    protected List<HoursDetail> hoursDetails;
    protected double totalHours;
    protected HoursStatus hoursStatus;

    public HoursReport(String userName, List<HoursDetail> hoursDetails)
    {
        setUserName(userName);
        setHoursDetails(hoursDetails);
    }

    public void setUserName(String userName)
    {
        boolean invalid = Strings.isNullOrEmpty(userName);
        if(invalid)
            throw new IllegalArgumentException("Username cannot be null or empty");
        this.userName = userName;
    }

    public void setHoursDetails(List<HoursDetail> hoursDetails)
    {
        this.hoursDetails = hoursDetails;
        setTotalHours();
    }

    private void setTotalHours()
    { // Sum all hours of each hours detail
        this.totalHours = this.hoursDetails.stream()
                .mapToDouble(HoursDetail::getHours)
                .sum();
        setHoursStatus(); // Update hours status
    }

    protected abstract void setHoursStatus();
}
