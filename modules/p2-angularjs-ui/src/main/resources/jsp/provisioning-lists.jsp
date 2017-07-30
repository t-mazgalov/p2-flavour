<div ng-controller="ProvisioningListsController">
    <md-content class="md-padding" layout-xs="column" layout="row">
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card>
                <md-card-header>
                    <md-card-avatar>
                        <md-icon class="material-icons">library_books</md-icon>
                    </md-card-avatar>
                    <md-card-header-text>
                        <span class="md-title">Available provisioning lists</span>
                        <span class="md-subhead">Manage provisioning lists</span>
                    </md-card-header-text>
                </md-card-header>
                <md-content>
                    <md-progress-linear md-mode="indeterminate" ng-disabled="disabledProgressListProvLists">
                    </md-progress-linear>
                    <md-list class="md-dense" flex>
                        <md-input-container class="md-block card-filter" flex="95">
                            <label>Filter provisioning lists</label>
                            <md-icon class="material-icons">search</md-icon>
                            <input ng-model="filterProvLists">
                        </md-input-container>
                        <md-divider></md-divider>
                        <md-list-item class="md-3-line"
                            ng-repeat="provList in provLists"
                            ng-show="([provList] | filter:{'name':filterProvLists}).length > 0">
                            <img
                                ng-src="img/icons/provisioning-list.png"
                                class="md-avatar"
                                alt="" />
                            <div class="md-list-item-text">
                                <h3>{{provList.name}}</h3>
                                <p>ID: {{provList.id}}</p>
                                <p>Extends: {{provList.extendedListIds}}</p>
                            </div>
                            <md-menu class="md-secondary">
                                <md-button class="md-icon-button">
                                    <md-icon class="material-icons">extension</md-icon>
                                </md-button>
                                <md-menu-content>
                                    <div ng-repeat="extendsProvList in provLists">
                                        <md-menu-item>
                                            <md-button ng-click="extendProvList(provList,extendsProvList)">
                                                <md-icon class="material-icons">add</md-icon>
                                                {{extendsProvList.name}}
                                            </md-button>
                                        </md-menu-item>
                                        <md-menu-item>
                                            <md-button ng-click="shrinkProvList(provList,extendsProvList)">
                                                <md-icon class="material-icons">remove</md-icon>
                                                {{extendsProvList.name}}
                                            </md-button>
                                        </md-menu-item>
                                    </div>
                                </md-menu-content>
                            </md-menu>
                        </md-list-item>
                    </md-list>
                </md-content>
            </md-card>
        </div>
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card ng-init="loadProvLists()">
                <md-card-header>
                  <md-card-avatar>
                    <md-icon class="material-icons">show_chart</md-icon>
                  </md-card-avatar>
                  <md-card-header-text>
                    <span class="md-title">Available P2 provisioning lists</span>
                    <span class="md-subhead">P2 provisioning lists DAG</span>
                  </md-card-header-text>
                </md-card-header>
                <md-content>
                    <md-progress-linear md-mode="indeterminate" ng-disabled="disabledProgressGenerateDAG">
                    </md-progress-linear>
                    <vis-network data="data" options="options"></vis-network>
                    <div layout-margin>
                        <md-checkbox ng-model="showSystemProvList" ng-change="loadProvLists()">
                            Show system
                        </md-checkbox>
                    </div>
                </md-content>
            </md-card>
        </div>
    </md-content>
</div>