package com.kotlarz.frontend.presenter.customers.reports.events;

import com.kotlarz.backend.domain.logs.FormatterConfigEntity;
import com.kotlarz.backend.domain.logs.ReportEntity;
import com.kotlarz.backend.service.logs.CustomerService;
import com.kotlarz.backend.service.logs.ReportService;
import com.kotlarz.frontend.dto.EventDto;
import com.kotlarz.frontend.presenter.Presenter;
import com.kotlarz.frontend.presenter.customers.reports.ReportsPresenter;
import com.kotlarz.frontend.presenter.customers.reports.events.filters.FiltersPresenter;
import com.kotlarz.frontend.ui.MainUI;
import com.kotlarz.frontend.util.LogFormatter;
import com.kotlarz.frontend.util.ParametersUtil;
import com.kotlarz.frontend.view.customers.reports.events.EventsView;
import com.kotlarz.frontend.view.customers.reports.events.filters.FiltersView;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringComponent
@ViewScope
public class EventsPresenter implements Presenter<EventsView> {
    public static final String REPORT_URL_CONST = "events";

    public static final Integer REPORT_ID_INDEX = 3;

    @Autowired
    private ReportService reportService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private FiltersPresenter filtersPresenter;

    @Autowired
    private MainUI mainUi;

    @Autowired
    private FiltersView filtersView;

    @Override
    public void handleNavigation(ViewChangeListener.ViewChangeEvent event, EventsView view) {
        List<String> parameters = ParametersUtil.resolve(event);
        Long reportId = Long.parseLong(parameters.get(REPORT_ID_INDEX));
        Long customerId = Long.parseLong(parameters.get(ReportsPresenter.CUSTOMER_ID_INDEX));

        Optional<ReportEntity> optionalReport = reportService.getReport(reportId);
        if (optionalReport.isPresent()) {
            ReportEntity report = optionalReport.get();
            view.setDownloadFilename(generateFileName(report.getDate(), report.getCustomer().getName()));

            Optional<FormatterConfigEntity> optionalFormatterConfig = customerService.getFormatterConfig(customerId);
            if (optionalFormatterConfig.isPresent()) {
                LogFormatter formatter = new LogFormatter(optionalFormatterConfig.get());
                List<EventDto> events = convertEvents(report);
                setLogs(formatter, events, view);
                initFilters(view, events, formatter);
            } else {
                // TODO
            }
        }
    }

    private List<EventDto> convertEvents(ReportEntity report) {
        return report.getEvents().stream()
                .map(EventDto::new)
                .collect(Collectors.toList());
    }

    private String generateFileName(Date reportDate, String customerName) {
        return customerName + "_" + reportDate.getTime() + ".log";
    }

    @Override
    public void initView(EventsView view) {
        view.addOnFiltersButtonClick(event -> mainUi.addWindow(filtersView));
    }

    private void initFilters(EventsView view, List<EventDto> events, LogFormatter formatter) {
        filtersPresenter.setOnFilterUpdated(filtered -> setLogs(formatter, filtered, view));
        filtersPresenter.initView(filtersView, events);
    }

    private void setLogs(LogFormatter formatter, List<EventDto> events, EventsView view) {
        String log = formatter.format(events).stream()
                .collect(Collectors.joining("\r\n"));
        view.setLog(log);
    }

}
