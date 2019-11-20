package com.msca.youtubedlgui.frontend;

import com.msca.youtubedlgui.backend.DownloadQueueService;
import com.msca.youtubedlgui.common.DownloadQueueEntity;
import com.msca.youtubedlgui.common.FormatType;
import com.msca.youtubedlgui.common.StatusType;
import com.sun.org.apache.xpath.internal.axes.PathComponent;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(value = "")
public class MainUi extends VerticalLayout {

    private DownloadQueueService service;

    private Grid<DownloadQueueEntity> grid;
    private TextField newAudioItemField;
    private TextField newVideoItemField;


    private <E> Grid.Column<E> addDateTime(Grid<E> grid, ValueProvider<E, LocalDateTime> provider, String header, String key) {
        return grid.addColumn(new LocalDateTimeRenderer<>(provider,
                DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")))
                .setHeader(header)
                .setSortable(true);
    }

    private <E> Grid.Column<E> addString(Grid<E> grid, ValueProvider<E, Object> provider, String header) {
        return grid.addColumn(provider)
                .setHeader(header)
                .setSortable(true);
    }

    public MainUi(DownloadQueueService service) {
        this.service = service;

        this.grid = new Grid<>(DownloadQueueEntity.class);
        this.newAudioItemField = new TextField();
        newAudioItemField.setPlaceholder("audio url...");
        this.newVideoItemField = new TextField();
        newVideoItemField.setPlaceholder("video url...");

        setSizeFull();
        {
            {
                HorizontalLayout actions = new HorizontalLayout();
                actions.setPadding(false);
                add(actions);
                {
                    newAudioItemField.setWidth("300px");
                    actions.add(newAudioItemField);

                    newVideoItemField.setWidth("300px");
                    actions.add(newVideoItemField);
                }
            }
            {
                HorizontalLayout gridLayout = new HorizontalLayout();
                gridLayout.setSizeFull();
                add(gridLayout);
                {
                    grid.setHeightFull();
                    gridLayout.add(grid);
                }
            }
        }


        grid.setColumns();
        addString(grid, DownloadQueueEntity::getStatus, "Status")
                .setWidth("30px")
                .setSortable(false);
        addString(grid, DownloadQueueEntity::getUrl, "Url");
        addString(grid, DownloadQueueEntity::getKey, "Key");
        addString(grid, DownloadQueueEntity::getFormat, "Format").setWidth("50px");
        addDateTime(grid, DownloadQueueEntity::getCreatedAt, "Created at", "");

        addDateTime(grid, DownloadQueueEntity::getDownloadedAt, "Downloaded at", "");
        addString(grid, DownloadQueueEntity::getMessage, "Message");


        grid.addComponentColumn(new ValueProvider<DownloadQueueEntity, HorizontalLayout>() {
            @Override
            public HorizontalLayout apply(DownloadQueueEntity queueItem) {

                HorizontalLayout layout = new HorizontalLayout();

                Button retry = new Button(VaadinIcon.REPLY.create());
                retry.addClickListener(e -> {
                    service.retry(queueItem.getId());
                    refreshList();
                });
                retry.setEnabled(StatusType.FINISHED.equals(queueItem.getStatus()) ||
                        StatusType.FAILED.equals(queueItem.getStatus()));
                layout.add(retry);


                Button download = new Button(VaadinIcon.DOWNLOAD.create());
                download.addClickListener(e -> {

                    UI.getCurrent().getPage().executeJavaScript("window.open(\"http://localhost:8080/static/"+queueItem.getId()+"\", \"_blank\");");
                });
                layout.add(download);

                Button delete = new Button(VaadinIcon.TRASH.create());
                delete.addClickListener(e -> {
                    service.delete(queueItem.getId());
                    refreshList();
                });
                layout.add(delete);

                Button remove = new Button(VaadinIcon.FILE_REMOVE.create());
                remove.addClickListener(e -> {
                    service.remove(queueItem.getId());
                    refreshList();
                });
                layout.add(remove);

                return layout;
            }
        });

        newVideoItemField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        newVideoItemField.addValueChangeListener(e -> newItem(e, FormatType.VIDEO_MP4));

        newAudioItemField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        newAudioItemField.addValueChangeListener(e -> newItem(e, FormatType.AUDIO_MP3));

        refreshList();

        //TODO: not works
//        grid.sort(Arrays.asList(new GridSortOrder(createdAt, SortDirection.DESCENDING)));

    }

    private void refreshList() {
        List<DownloadQueueEntity> list = new ArrayList<>();
        service.listAll().forEach(list::add);
        grid.setItems(list);
    }

    private void newItem(AbstractField.ComponentValueChangeEvent e, FormatType type) {
        if (e.getHasValue().isEmpty()) {
            return;
        }

        String value = getValue(type);
        if (value != null && !value.trim().isEmpty()) {
            service.add(type, value);
            Notification.show("Add: " + value);
        }

        newAudioItemField.setValue("");
        newVideoItemField.setValue("");

        refreshList();
    }

    private String getValue(FormatType type) {
        switch (type) {
            case AUDIO_MP3:
                return newAudioItemField.getValue();
            case VIDEO_MP4:
                return newVideoItemField.getValue();
        }
        return null;
    }
}
