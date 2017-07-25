<div ng-controller="ProfilesController">
    <md-content class="md-padding" layout-xs="column" layout="row">
        <div flex-xs flex-gt-xs="50" layout="column">
            <md-card>
                <md-card-header>
                  <md-card-avatar>
                    <md-icon class="material-icons">library_books</md-icon>
                  </md-card-avatar>
                  <md-card-header-text>
                    <span class="md-title">Available P2 profiles</span>
                    <span class="md-subhead">List of available P2 profile for switch</span>
                  </md-card-header-text>
                </md-card-header>
                <md-content layout-padding>
                    <p class="md-caption">
                        Currently using profile:
                        {{ currentProfile ? currentProfile.name + ':' + currentProfile.location : 'Not set' }}
                    </p>
                    <md-select
                        placeholder="Select P2 profile"
                        ng-model="selectedProfile"
                        md-on-open="loadProfiles()"
                        ng-change="changeProfile()">
                        <md-option ng-value="profile" ng-repeat="profile in profiles">
                            {{profile.name}}:{{profile.id}} ({{profile.location}})
                        </md-option>
                    </md-select>
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
        </div>
    </md-content>
</div>