<md-dialog aria-label="Create P2 provisioning list">
  <form ng-cloak>
    <md-toolbar>
      <div class="md-toolbar-tools">
        <h2>Create P2 provisioning list</h2>
        <span flex></span>
        <md-button class="md-icon-button" ng-click="cancel()">
          <md-icon class="material-icons" aria-label="Close dialog">close</md-icon>
        </md-button>
      </div>
    </md-toolbar>

    <md-dialog-content>
      <div class="md-dialog-content">
        <md-select
            placeholder="Select P2 profile"
            ng-model="selectedProfile"
            md-on-open="loadProfiles()">
            <md-option ng-value="profile" ng-repeat="profile in profiles">
                {{profile.name}}:{{profile.id}} ({{profile.location}})
            </md-option>
        </md-select>
        <md-input-container class="md-block" flex-gt-sm>
            <label>Provisioning list name</label>
            <input ng-model="provListName">
        </md-input-container>
      </div>
    </md-dialog-content>

    <md-dialog-actions layout="row">
      <md-button ng-click="cancel()">
       Cancel
      </md-button>
      <md-button ng-click="createProvList()" class="md-primary">
        Create
      </md-button>
    </md-dialog-actions>
  </form>
</md-dialog>