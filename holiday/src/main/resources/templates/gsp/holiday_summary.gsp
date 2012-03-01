<script type="text/javascript" src="/js/holiday_summary.js"></script>

<h1>Summary</h1>

<script type="text/html" id="calendarSelector">
<img class="minusMonth" src="/images/previous.png"/>
<span id="month_current" style="width:300px;">{{month}} {{year}}</span>
<img class="plusMonth" src="/images/next.png"/>
</script>

<script type="text/html" id="1-month-template"><%= i18n.t("january") %></script>
<script type="text/html" id="2-month-template"><%= i18n.t("february") %></script>
<script type="text/html" id="3-month-template"><%= i18n.t("march") %></script>
<script type="text/html" id="4-month-template"><%= i18n.t("april") %></script>
<script type="text/html" id="5-month-template"><%= i18n.t("may") %></script>
<script type="text/html" id="6-month-template"><%= i18n.t("june") %></script>
<script type="text/html" id="7-month-template"><%= i18n.t("july") %></script>
<script type="text/html" id="8-month-template"><%= i18n.t("august") %></script>
<script type="text/html" id="9-month-template"><%= i18n.t("september") %></script>
<script type="text/html" id="10-month-template"><%= i18n.t("october") %></script>
<script type="text/html" id="11-month-template"><%= i18n.t("november") %></script>
<script type="text/html" id="12-month-template"><%= i18n.t("december") %></script>
<div id="monthSelector"></div>
<div id="holiday-summary"></div>