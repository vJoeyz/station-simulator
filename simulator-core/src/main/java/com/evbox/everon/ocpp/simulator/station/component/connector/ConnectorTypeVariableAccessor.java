package com.evbox.everon.ocpp.simulator.station.component.connector;

import com.evbox.everon.ocpp.common.CiString;
import com.evbox.everon.ocpp.simulator.station.Station;
import com.evbox.everon.ocpp.simulator.station.StationStore;
import com.evbox.everon.ocpp.simulator.station.component.variable.SetVariableValidator;
import com.evbox.everon.ocpp.simulator.station.component.variable.VariableAccessor;
import com.evbox.everon.ocpp.simulator.station.component.variable.VariableGetter;
import com.evbox.everon.ocpp.simulator.station.component.variable.VariableSetter;
import com.evbox.everon.ocpp.simulator.station.component.variable.attribute.AttributePath;
import com.evbox.everon.ocpp.simulator.station.component.variable.attribute.AttributeType;
import com.evbox.everon.ocpp.simulator.station.evse.Connector;
import com.evbox.everon.ocpp.simulator.station.evse.Evse;
import com.evbox.everon.ocpp.v201.message.centralserver.*;
import com.evbox.everon.ocpp.v201.message.centralserver.Attribute;
import com.evbox.everon.ocpp.v201.message.station.EVSE;
import com.evbox.everon.ocpp.v201.message.station.Variable;
import com.evbox.everon.ocpp.v201.message.station.*;
import com.evbox.everon.ocpp.v201.message.station.Component;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.evbox.everon.ocpp.v201.message.station.Data.SEQUENCE_LIST;
import static java.util.Collections.singletonList;

public class ConnectorTypeVariableAccessor extends VariableAccessor {

    public static final String NAME = "ConnectorType";
    public static final String CONNECTOR_TYPE = "CHAdeMO";

    private final Map<AttributeType, VariableGetter> variableGetters = ImmutableMap.<AttributeType, VariableGetter>builder()
            .put(AttributeType.ACTUAL, this::getActualValue)
            .build();

    private final Map<AttributeType, SetVariableValidator> variableValidators = ImmutableMap.<AttributeType, SetVariableValidator>builder()
            .put(AttributeType.ACTUAL, this::validateActualValue)
            .build();

    public ConnectorTypeVariableAccessor(Station station, StationStore stationStore) {
        super(station, stationStore);
    }

    @Override
    public String getVariableName() {
        return NAME;
    }

    @Override
    public Map<AttributeType, VariableGetter> getVariableGetters() {
        return variableGetters;
    }

    @Override
    public Map<AttributeType, VariableSetter> getVariableSetters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<AttributeType, SetVariableValidator> getVariableValidators() {
        return variableValidators;
    }

    @Override
    public List<ReportData> generateReportData(String componentName) {
        List<ReportData> reportData = new ArrayList<>();

        for (Evse evse : getStationStore().getEvses()) {
            for (Connector connector : evse.getConnectors()) {
                EVSE componentEvse = new EVSE()
                        .withConnectorId(connector.getId())
                        .withId(evse.getId());

                Component component = new Component()
                        .withName(new CiString.CiString50(componentName))
                        .withEvse(componentEvse);

                VariableAttribute variableAttribute = new VariableAttribute()
                        .withValue(new CiString.CiString2500(CONNECTOR_TYPE))
                        .withPersistent(true)
                        .withConstant(true)
                        .withMutability(Mutability.READ_ONLY);

                VariableCharacteristics variableCharacteristics = new VariableCharacteristics()
                        .withDataType(SEQUENCE_LIST)
                        .withSupportsMonitoring(false);

                ReportData reportDatum = new ReportData()
                        .withComponent(component)
                        .withVariable(new Variable().withName(new CiString.CiString50(NAME)))
                        .withVariableCharacteristics(variableCharacteristics)
                        .withVariableAttribute(singletonList(variableAttribute));

                reportData.add(reportDatum);
            }
        }

        return reportData;
    }

    @Override
    public boolean isMutable() { return false; }

    private SetVariableResult validateActualValue(AttributePath attributePath, CiString.CiString1000 attributeValue) {
        return RESULT_CREATOR.createResult(attributePath, attributeValue, SetVariableStatus.REJECTED);
    }

    private GetVariableResult getActualValue(AttributePath attributePath) {
        Integer evseId = attributePath.getComponent().getEvse().getId();
        Integer connectorId = attributePath.getComponent().getEvse().getConnectorId();

        boolean connectorExists = getStationStore().tryFindConnector(evseId, connectorId).isPresent();

        GetVariableResult getVariableResult = new GetVariableResult()
                .withComponent(attributePath.getComponent())
                .withVariable(attributePath.getVariable())
                .withAttributeType(Attribute.fromValue(attributePath.getAttributeType().getName()));

        if (!connectorExists) {
            return getVariableResult
                    .withAttributeStatus(GetVariableStatus.REJECTED);
        } else {
            return getVariableResult
                    .withAttributeValue(new CiString.CiString2500(CONNECTOR_TYPE))
                    .withAttributeStatus(GetVariableStatus.ACCEPTED);
        }
    }
}
