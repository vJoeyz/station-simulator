package com.evbox.everon.ocpp.simulator.station.actions.user;

import com.evbox.everon.ocpp.common.OptionList;
import com.evbox.everon.ocpp.simulator.station.StationMessageSender;
import com.evbox.everon.ocpp.simulator.station.StationStore;
import com.evbox.everon.ocpp.simulator.station.component.transactionctrlr.TxStartStopPointVariableValues;
import com.evbox.everon.ocpp.simulator.station.evse.CableStatus;
import com.evbox.everon.ocpp.simulator.station.evse.Connector;
import com.evbox.everon.ocpp.simulator.station.evse.Evse;
import com.evbox.everon.ocpp.simulator.station.evse.StateManager;
import com.evbox.everon.ocpp.simulator.station.evse.states.AvailableState;
import com.evbox.everon.ocpp.simulator.station.evse.states.StoppedState;
import com.evbox.everon.ocpp.simulator.station.evse.states.WaitingForAuthorizationState;
import com.evbox.everon.ocpp.simulator.station.subscription.Subscriber;
import com.evbox.everon.ocpp.v201.message.station.Reason;
import com.evbox.everon.ocpp.v201.message.station.StatusNotificationRequest;
import com.evbox.everon.ocpp.v201.message.station.StatusNotificationResponse;
import com.evbox.everon.ocpp.v201.message.station.TriggerReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.evbox.everon.ocpp.mock.constants.StationConstants.DEFAULT_CONNECTOR_ID;
import static com.evbox.everon.ocpp.mock.constants.StationConstants.DEFAULT_EVSE_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnplugTest {

    @Mock
    Connector connectorMock;
    @Mock
    Evse evseMock;
    @Mock
    StationStore stationStoreMock;
    @Mock
    StationMessageSender stationMessageSenderMock;
    @Mock
    StateManager stateManagerMock;

    Unplug unplug;

    @BeforeEach
    void setUp() {
        this.unplug = new Unplug(DEFAULT_EVSE_ID, DEFAULT_CONNECTOR_ID);
        this.stateManagerMock = new StateManager(null, stationStoreMock, stationMessageSenderMock);
        when(evseMock.getEvseState()).thenReturn(new AvailableState());
        when(evseMock.findConnector(anyInt())).thenReturn(connectorMock);
        when(stationStoreMock.findEvse(anyInt())).thenReturn(evseMock);
    }

    @Test
    void shouldThrowExceptionWhenStateIsLocked() {

        when(evseMock.getEvseState()).thenReturn(new WaitingForAuthorizationState());
        when(stationStoreMock.findEvse(anyInt())).thenReturn(evseMock);
        when(evseMock.findConnector(anyInt()).getCableStatus()).thenReturn(CableStatus.LOCKED);

        assertThrows(IllegalStateException.class, () -> unplug.perform(stateManagerMock));

    }

    @Test
    void verifyTransactionStatusNotification() {

        when(evseMock.getEvseState()).thenReturn(new StoppedState());
        when(stationStoreMock.getTxStopPointValues()).thenReturn(new OptionList<>(Collections.singletonList(TxStartStopPointVariableValues.EV_CONNECTED)));

        unplug.perform(stateManagerMock);

        ArgumentCaptor<Subscriber<StatusNotificationRequest, StatusNotificationResponse>> subscriberCaptor = ArgumentCaptor.forClass(Subscriber.class);

        verify(stationMessageSenderMock).sendStatusNotificationAndSubscribe(any(Evse.class), any(Connector.class), subscriberCaptor.capture());

        subscriberCaptor.getValue().onResponse(new StatusNotificationRequest(), new StatusNotificationResponse());

        verify(stationMessageSenderMock).sendTransactionEventEnded(anyInt(), anyInt(), any(TriggerReason.class),
                nullable(Reason.class), anyLong());

    }

}
