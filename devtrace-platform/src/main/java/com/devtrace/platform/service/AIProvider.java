package com.devtrace.platform.service;

import com.devtrace.platform.dto.IncidentReport;
import com.devtrace.platform.dto.InvestigationContext;

public interface AIProvider {
    IncidentReport generateInvestigationReport(InvestigationContext context);
}
