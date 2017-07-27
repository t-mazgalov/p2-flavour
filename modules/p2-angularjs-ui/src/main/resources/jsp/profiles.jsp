<div ng-controller="ProfilesController">
    <md-content class="md-padding" layout-xs="column" layout="row">
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card ng-init="loadProfiles()">
                <md-card-header>
                  <md-card-avatar>
                    <md-icon class="material-icons">library_books</md-icon>
                  </md-card-avatar>
                  <md-card-header-text>
                    <span class="md-title">Available P2 profiles</span>
                    <span class="md-subhead">List of available P2 profiles</span>
                  </md-card-header-text>
                </md-card-header>
                <md-content>
                    <md-progress-linear md-mode="indeterminate" ng-disabled="disabledProgressListProfiles">
                    </md-progress-linear>
                    <md-list
                        class="md-dense"
                        ng-repeat="profile in profiles"
                        flex>
                        <md-list-item class="md-3-line">
                           <img ng-src="img/icons/profile.png" class="md-avatar" alt="Profile" />
                           <div class="md-list-item-text">
                               <h3>{{profile.name}}</h3>
                               <p>{{profile.location}}</p>
                               <p>Profile ID: {{profile.id}}</p>
                           </div>
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
                        <span class="md-title">Create P2 profile</span>
                        <span class="md-subhead">Select P2 profile name and location</span>
                    </md-card-header-text>
                </md-card-header>
                <md-content layout-padding>
                    <md-input-container class="md-block" flex-gt-sm>
                        <label>P2 profile name</label>
                        <input ng-model="profileName">
                    </md-input-container>
                    <md-input-container class="md-block" flex-gt-sm>
                        <label>P2 profile location</label>
                        <input ng-model="profileLocation">
                    </md-input-container>
                    <md-button ng-click="createProfile()" class="md-raised md-primary">Create</md-button>
                </md-content>
            </md-card>
            <md-card>
                <md-card-header>
                  <md-card-avatar>
                    <md-icon class="material-icons">settings</md-icon>
                  </md-card-avatar>
                  <md-card-header-text>
                    <span class="md-title">Set system profile</span>
                    <span class="md-subhead">Change the P2 system profile</span>
                  </md-card-header-text>
                </md-card-header>
                <md-content>
                    <md-progress-linear md-mode="indeterminate" ng-disabled="disabledProgressChangeProfiles">
                    </md-progress-linear>
                    <div layout-padding>
                        <p class="md-caption">
                            Current system profile:
                            {{ currentProfile ? currentProfile.name + ':' + currentProfile.location : 'Not set' }}
                        </p>
                        <md-select
                            placeholder="Select P2 profile"
                            ng-model="selectedProfile"
                            ng-init="checkCurrentProfile()"
                            md-on-open="loadProfiles()"
                            ng-change="changeProfile()">
                            <md-option ng-value="profile" ng-repeat="profile in profiles">
                                {{profile.name}}:{{profile.location}}
                            </md-option>
                        </md-select>
                    </div>
                </md-content>
            </md-card>
        </div>
    </md-content>
</div>