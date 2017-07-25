<md-card>
    <md-card-title>
        <md-card-title-text>
            <span class="md-headline">Available P2 installable units</span>
            <span class="md-subhead">Select installable units for installation</span>
        </md-card-title-text>
    </md-card-title>
    <md-list ng-controller="IUsListCtrl" ng-cloak>
        <md-list-item ng-repeat="iu in ius">
           <md-checkbox ng-model="iu.selected"></md-checkbox>
           <p>{{iu.id}}</p>
        </md-list-item>
    </md-list>
    <div layout="row" layout-sm="column">
        <md-button class="md-raised md-primary">Install</md-button>
    </div>
</md-card>
