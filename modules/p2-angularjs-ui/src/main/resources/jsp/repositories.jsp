<div ng-controller="RepositoriesController">
    <md-content class="md-padding" layout-xs="column" layout="row">
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card>
                <md-card-header>
                  <md-card-avatar>
                    <md-icon class="material-icons">library_books</md-icon>
                  </md-card-avatar>
                  <md-card-header-text>
                    <span class="md-title">Available P2 repositories</span>
                    <span class="md-subhead">List of available P2 repositories</span>
                  </md-card-header-text>
                </md-card-header>
                <md-content>
                    <md-progress-linear md-mode="indeterminate" ng-disabled="disabledProgressListRepos">
                    </md-progress-linear>
                    <md-input-container layout="row" layout-align="center center" >
                        <label>Filter P2 profile repositories</label>
                        <md-select
                            ng-model="filerProfileRepos"
                            ng-init="checkCurrentProfile()"
                            md-on-open="loadProfiles()"
                            class="md-block" flex="95">
                            <md-option ng-value="profile.name" ng-repeat="profile in profiles">
                                {{profile.name}}:{{profile.location}}
                            </md-option>
                        </md-select>
                    </md-input-container>
                    <md-divider></md-divider>
                    <md-list
                        class="md-dense"
                        ng-repeat="repo in repos | filter: filterRepos"
                        flex>
                        <md-list-item class="md-3-line" ng-repeat="metadataRepository in repo.metadataRepositories">
                           <img ng-src="img/icons/metadata.png" class="md-avatar" alt="Metadata Repository" />
                           <div class="md-list-item-text">
                               <h3>{{metadataRepository}}</h3>
                               <p>Profile name: {{repo.profile.name}}, location: {{repo.profile.location}}</p>
                               <p>Profile ID: {{repo.profile.id}}</p>
                           </div>
                           <md-icon class="material-icons"
                             ng-click="loadIusGraph(repo.profile.id, metadataRepository)">
                             show_chart
                           </md-icon>
                           <md-icon class="material-icons"
                               ng-click="listMetadataRepo(repo.profile.id, metadataRepository)">
                               find_in_page
                           </md-icon>
                        </md-list-item>
                        <md-list-item class="md-3-line" ng-repeat="artifactsRepository in repo.artifactsRepositories">
                           <img ng-src="img/icons/artifacts.png" class="md-avatar" alt="Artifact Repository" />
                           <div class="md-list-item-text">
                               <h3>{{artifactsRepository}}</h3>
                               <p>Profile name: {{repo.profile.name}}, location: {{repo.profile.location}}</p>
                               <p>Profile ID: {{repo.profile.id}}</p>
                           </div>
                           <md-icon class="material-icons"
                               ng-click="listArtifactsRepo(repo.profile.id, artifactsRepository)">
                               find_in_page
                           </md-icon>
                        </md-list-item>
                    </md-list>
                </md-content>
            </md-card>
        </div>
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card>
                <md-card-header>
                    <md-card-avatar>
                        <md-icon class="material-icons">library_add</md-icon>
                    </md-card-avatar>
                    <md-card-header-text>
                        <span class="md-title">Load P2 repository</span>
                        <span class="md-subhead">Select P2 repository location</span>
                    </md-card-header-text>
                </md-card-header>
                <md-content layout-padding>
                    <md-select
                        placeholder="Select P2 profile"
                        ng-model="selectedProfile"
                        md-on-open="loadProfiles()">
                        <md-option ng-value="profile" ng-repeat="profile in profiles">
                            {{profile.name}} ({{profile.location}})
                        </md-option>
                    </md-select>
                    <md-input-container class="md-block" flex-gt-sm>
                        <label>P2 Metadata Repository</label>
                        <input ng-model="metadataLocation">
                    </md-input-container>
                    <md-input-container class="md-block" flex-gt-sm>
                        <label>P2 Artifact Repository</label>
                        <input ng-model="artifactsLocation">
                    </md-input-container>
                    <md-button ng-click="loadRepo()" class="md-raised md-primary">Load</md-button>
                </md-content>
            </md-card>
        </div>
    </md-content>
    <md-content class="md-padding" layout-xs="column" layout="row">
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card ng-show="loadedRepoCard">
                <md-card-header>
                    <md-card-avatar>
                        <md-icon class="material-icons">done</md-icon>
                    </md-card-avatar>
                    <md-card-header-text>
                        <span class="md-title">P2 repository summary</span>
                        <span class="md-subhead">{{listedRepoLocation}}</span>
                    </md-card-header-text>
                </md-card-header>
                <md-content>
                    <md-progress-linear md-mode="indeterminate" ng-disabled="disabledProgressListRepo">
                    </md-progress-linear>
                    <md-list class="md-dense" flex>
                        <md-input-container class="md-block card-filter" flex="95">
                            <label>Filter units</label>
                            <md-icon class="material-icons">search</md-icon>
                            <input ng-model="filterRepoUnits">
                        </md-input-container>
                        <md-divider></md-divider>
                        <md-list-item class="md-3-line"
                            ng-repeat="iu in ius"
                            ng-show="([iu] | filter:{'id':filterRepoUnits}).length > 0">
                            <img
                                ng-src="img/icons/{{iu.iuType}}.png"
                                class="md-avatar"
                                alt="{{iu.iuType}}" />
                            <div class="md-list-item-text">
                                <h3>{{iu.id}}</h3>
                                <p>Version:
                                    {{iu.version.major}}.{{iu.version.minor}}.{{iu.version.micro}}.{{iu.version.qualifier}}
                                </p>
                                <p>Unit type: {{iu.iuType}}</p>
                            </div>
                            <md-icon class="material-icons"
                                 ng-click="loadIuGraph(selectedProfileId, listedRepoLocation, iu.id, iu.version)">
                                 show_chart
                               </md-icon>
                            <md-menu class="md-secondary" ng-if="iu.iuType !== 'artifact-key'">
                                <md-button class="md-icon-button">
                                    <md-icon class="material-icons">add</md-icon>
                                </md-button>
                                <md-menu-content>
                                    <div ng-repeat="provList in provLists">
                                        <md-menu-item>
                                            <md-button ng-click="addProvListIUs(provList,iu,listedRepoLocation)">
                                                <md-icon class="material-icons">add_box</md-icon>
                                                {{provList.name}}
                                            </md-button>
                                        </md-menu-item>
                                    </div>
                                    <md-menu-divider></md-menu-divider>
                                    <md-menu-item>
                                        <md-button ng-click="showCreateListDialog($event,iu,listedRepoLocation)">
                                            <md-icon class="material-icons">create</md-icon>
                                            Create new IUs list
                                        </md-button>
                                    </md-menu-item>
                                </md-menu-content>
                            </md-menu>
                        </md-list-item>
                    </md-list>
                </md-content>
            </md-card>
        </div>
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card ng-show="iusGraphData">
                <md-card-header>
                  <md-card-avatar>
                    <md-icon class="material-icons">show_chart</md-icon>
                  </md-card-avatar>
                  <md-card-header-text>
                    <span class="md-title">Installable units dependencies graph</span>
                    <span class="md-subhead">Installable units requirements and capabilities</span>
                  </md-card-header-text>
                </md-card-header>
                <md-content>
                    <md-progress-linear md-mode="indeterminate" ng-disabled="disabledProgressIusGraph">
                    </md-progress-linear>
                    <vis-network data="iusGraphData" options="options" events="iusGraphEvents"></vis-network>
                </md-content>
            </md-card>
            <md-card ng-show="currentProvList">
                <md-card-header>
                    <md-card-avatar>
                        <md-icon class="material-icons">view_list</md-icon>
                    </md-card-avatar>
                    <md-card-header-text>
                        <span class="md-title">Provisioning list: {{currentProvList.name}}</span>
                        <span class="md-subhead">ID: {{currentProvList.id}}</span>
                    </md-card-header-text>
                </md-card-header>
                <md-content>
                    <md-progress-linear md-mode="indeterminate" ng-disabled="disabledProgressManageProvList">
                    </md-progress-linear>
                    <md-list class="md-dense" flex>
                        <md-input-container class="md-block card-filter" flex="95">
                            <label>Filter units</label>
                            <md-icon class="material-icons">search</md-icon>
                            <input ng-model="filterProvListUnits">
                        </md-input-container>
                        <md-divider></md-divider>
                        <md-list-item class="md-3-line" ng-repeat="iu in currentProvList.installableUnits"
                            ng-show="([iu] | filter:{'id':filterProvListUnits}).length > 0">
                            <img
                                ng-src="img/icons/{{iu.type}}.png"
                                class="md-avatar"
                                alt="{{iu.type}}" />
                            <div class="md-list-item-text">
                                <h3>{{iu.id}}</h3>
                                <p>Version:
                                    {{iu.major}}.{{iu.minor}}.{{iu.micro}}.{{iu.qualifier}}
                                </p>
                                <p>Unit type: {{iu.type}}</p>
                            </div>
                            <md-icon class="material-icons" ng-click="removeProvListIUs(currentProvList,iu)">
                                remove
                            </md-icon>
                        </md-list-item>
                    </md-list>
                </md-content>
            </md-card>
        </div>
    </md-content>
</div>