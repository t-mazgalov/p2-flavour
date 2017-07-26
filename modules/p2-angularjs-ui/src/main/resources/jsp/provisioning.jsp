<div ng-controller="ProvisioningController">
    <md-content class="md-padding" layout-xs="column" layout="row">
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card>
                <md-card-header>
                  <md-card-avatar>
                    <md-icon class="material-icons">library_books</md-icon>
                  </md-card-avatar>
                  <md-card-header-text>
                    <span class="md-title">Available P2 provisioning lists</span>
                    <span class="md-subhead">List of available P2 provisioning lists</span>
                  </md-card-header-text>
                </md-card-header>
                <md-content layout-padding>
                    <md-select
                        placeholder="Select P2 provisioning lists"
                        ng-model="selectedProvList"
                        md-on-open="loadProvLists()">
                        <md-option ng-value="provList" ng-repeat="provList in provLists">
                            {{provList.name}}
                        </md-option>
                    </md-select>
                    <md-select
                        placeholder="Select P2 profile"
                        ng-model="selectedProfile"
                        md-on-open="loadProfiles()">
                        <md-option ng-value="profile" ng-repeat="profile in profiles">
                            {{profile.name}}:{{profile.location}}
                        </md-option>
                    </md-select>
                    <md-button ng-click="provisionProvList()" class="md-raised md-primary">Provision</md-button>
                </md-content>
            </md-card>
        </div>
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card ng-if="selectedProvList">
                <md-card-header>
                    <md-card-avatar>
                        <md-icon class="material-icons">view_list</md-icon>
                    </md-card-avatar>
                    <md-card-header-text>
                        <span class="md-title">Provisioning list: {{selectedProvList.name}}</span>
                        <span class="md-subhead">ID: {{selectedProvList.id}}</span>
                    </md-card-header-text>
                </md-card-header>
                <md-content>
                    <md-list class="md-dense" flex>
                        <md-list-item class="md-3-line" ng-repeat="iu in selectedProvList.installableUnits">
                            <img
                                ng-src="img/icons/{{iu.type}}.png"
                                class="md-avatar"
                                alt="{{iu.type}}" />
                            <div class="md-list-item-text">
                                <h3>{{iu.id}}</h3>
                                <p>Version:
                                    {{iu.major}}.{{iu.minor}}.{{iu.micro}}.{{iu.qualifier}}
                                </p>
                                <p>Repository: {{iu.repository}}</p>
                            </div>
                        </md-list-item>
                    </md-list>
                </md-content>
            </md-card>
        </div>
    </md-content>
</div>